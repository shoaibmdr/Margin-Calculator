package com.margincalculator.service;

import com.margincalculator.dto.LimitStatus;
import com.margincalculator.dto.MarginAction;
import com.margincalculator.dto.MarginReportDTO;
import com.margincalculator.entity.*;
import com.margincalculator.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Core service that calculates margin metrics for each client.
 *
 * <p>Business logic:
 * <pre>
 * Total Market Value     = Market Value of Purchased Shares
 *                        + Market Value of Pledged Shares (60%)
 * Current Equity         = Total Market Value - Loan Outstanding
 * Current Equity %       = (Current Equity / Total Market Value) * 100
 *
 * Action:
 *   currentEquityPct >= initialMarginPct            → HEALTHY
 *   triggerMarginPct <= currentEquityPct < maintenanceMarginPct → MARGIN_CALL
 *   currentEquityPct < triggerMarginPct              → FORCE_SELL
 *
 * Shortfall Amount (when not HEALTHY):
 *   shortfall = (maintenanceMarginPct / 100 * totalMarketValue) - currentEquity
 *   (capped at 0 if positive, i.e. no shortfall)
 * </pre>
 *
 * <p>After each calculation the result is persisted in {@code margin_monitoring_snapshot}
 * so that operators always have an up-to-date monitoring table with the last-modified date.
 */
@Service
@RequiredArgsConstructor
public class MarginCalculationService {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int SCALE = 2;
    private static final int EXPIRING_SOON_DAYS = 30;

    private final ClientRepository clientRepository;
    private final ClientwiseInventoryRepository clientwiseInventoryRepository;
    private final PledgeClientwiseInventoryRepository pledgeInventoryRepository;
    private final MarginMonitoringSnapshotRepository snapshotRepository;

    /**
     * Generate margin report for all clients and refresh the monitoring table.
     */
    @Transactional
    public List<MarginReportDTO> generateReportForAllClients() {
        return clientRepository.findAll()
                .stream()
                .map(this::buildAndPersistMarginReport)
                .collect(Collectors.toList());
    }

    /**
     * Generate margin report for a specific client and refresh its monitoring row.
     */
    @Transactional
    public MarginReportDTO generateReportForClient(String clientId) {
        Client client = clientRepository.findById(clientId)
                .orElseThrow(() -> new IllegalArgumentException("Client not found: " + clientId));
        return buildAndPersistMarginReport(client);
    }

    /**
     * Return the current monitoring table (all snapshots ordered by client ID).
     * Snapshots reflect the state as of the last margin calculation run.
     */
    @Transactional(readOnly = true)
    public List<MarginMonitoringSnapshot> getMonitoringTable() {
        List<MarginMonitoringSnapshot> rows = snapshotRepository.findAllByOrderByClientIdAsc();
        for (int i = 0; i < rows.size(); i++) {
            rows.get(i).setSn(i + 1);
        }
        return rows;
    }

    // -------------------------------------------------------------------------
    // Internal helpers
    // -------------------------------------------------------------------------

    private MarginReportDTO buildAndPersistMarginReport(Client client) {
        MarginReportDTO dto = buildMarginReport(client);
        persistSnapshot(client, dto);
        return dto;
    }

    /**
     * Build the margin report DTO for a single client.
     */
    private MarginReportDTO buildMarginReport(Client client) {
        String clientId = client.getClientId();

        // 1. Market Value of Purchased Shares
        BigDecimal mvPurchased = clientwiseInventoryRepository
                .sumMarketValueByClientId(clientId)
                .setScale(SCALE, RoundingMode.HALF_UP);

        // 2. Market Value of Pledged Shares (60%)
        BigDecimal mvPledged60 = pledgeInventoryRepository
                .sumMarketValue60PctByClientId(clientId)
                .setScale(SCALE, RoundingMode.HALF_UP);

        // 3. Total Market Value
        BigDecimal totalMarketValue = mvPurchased.add(mvPledged60);

        // 4. Loan Outstanding
        BigDecimal loanOutstanding = client.getLoanOutstanding();

        // 5. Current Equity
        BigDecimal currentEquity = totalMarketValue.subtract(loanOutstanding);

        // 6. Current Equity %
        BigDecimal currentEquityPct = BigDecimal.ZERO;
        if (totalMarketValue.compareTo(BigDecimal.ZERO) > 0) {
            currentEquityPct = currentEquity
                    .multiply(HUNDRED)
                    .divide(totalMarketValue, SCALE, RoundingMode.HALF_UP);
        }

        // 7. Margin thresholds
        BigDecimal initialMarginPct = client.getInitialMarginPct();
        BigDecimal maintenanceMarginPct = client.getMaintenanceMarginPct();
        BigDecimal triggerMarginPct = client.getTriggerMarginPct();

        // 8. Determine action
        MarginAction action = determineAction(currentEquityPct, initialMarginPct, triggerMarginPct);

        // 9. Calculate shortfall
        BigDecimal shortfall = calculateShortfall(action, currentEquity, totalMarketValue, maintenanceMarginPct);

        // 10. Limit expiry fields
        LocalDate expiryDate = client.getLimitExpiryDate();
        Long daysRemaining = expiryDate != null ? ChronoUnit.DAYS.between(LocalDate.now(), expiryDate) : null;
        LimitStatus limitStatus = deriveLimitStatus(daysRemaining);

        return MarginReportDTO.builder()
                .clientId(clientId)
                .clientName(client.getClientName())
                .marketValuePurchasedShares(mvPurchased)
                .marketValuePledgedShares60Pct(mvPledged60)
                .totalMarketValue(totalMarketValue)
                .loanOutstanding(loanOutstanding)
                .currentEquity(currentEquity)
                .currentEquityPct(currentEquityPct)
                .initialMarginPct(initialMarginPct)
                .maintenanceMarginPct(maintenanceMarginPct)
                .triggerMarginPct(triggerMarginPct)
                .shortfallAmount(shortfall)
                .action(action)
                .clientPan(client.getClientPan())
                .clientMobileNumber(client.getClientMobileNumber())
                .approvedMarginTradingLimit(client.getApprovedMarginTradingLimit())
                .approvedLoanLimit(client.getApprovedLoanLimit())
                .limitApprovedDate(client.getLimitApprovedDate())
                .limitExpiryDate(expiryDate)
                .daysRemainingBeforeExpiry(daysRemaining)
                .limitStatus(limitStatus)
                .build();
    }

    /**
     * Upsert the monitoring snapshot row for this client.
     */
    private void persistSnapshot(Client client, MarginReportDTO dto) {
        MarginMonitoringSnapshot snapshot = snapshotRepository
                .findByClientId(client.getClientId())
                .orElse(MarginMonitoringSnapshot.builder().clientId(client.getClientId()).build());

        snapshot.setClientName(dto.getClientName());
        snapshot.setMarketValuePurchasedShares(dto.getMarketValuePurchasedShares());
        snapshot.setMarketValuePledgedShares60Pct(dto.getMarketValuePledgedShares60Pct());
        snapshot.setTotalMarketValue(dto.getTotalMarketValue());
        snapshot.setLoanOutstanding(dto.getLoanOutstanding());
        snapshot.setCurrentEquity(dto.getCurrentEquity());
        snapshot.setCurrentEquityPct(dto.getCurrentEquityPct());
        snapshot.setInitialMarginPct(dto.getInitialMarginPct());
        snapshot.setMaintenanceMarginPct(dto.getMaintenanceMarginPct());
        snapshot.setTriggerMarginPct(dto.getTriggerMarginPct());
        snapshot.setStatusAction(dto.getAction() != null ? dto.getAction().name() : null);
        snapshot.setShortfallAmount(dto.getShortfallAmount());
        snapshot.setClientPan(dto.getClientPan());
        snapshot.setClientMobileNumber(dto.getClientMobileNumber());
        snapshot.setApprovedMarginTradingLimit(dto.getApprovedMarginTradingLimit());
        snapshot.setApprovedLoanLimit(dto.getApprovedLoanLimit());
        snapshot.setLimitApprovedDate(dto.getLimitApprovedDate());
        snapshot.setLimitExpiryDate(dto.getLimitExpiryDate());
        snapshot.setDaysRemainingBeforeExpiry(dto.getDaysRemainingBeforeExpiry());
        snapshot.setLimitStatus(dto.getLimitStatus());
        snapshot.setLastModifiedDate(LocalDateTime.now());

        snapshotRepository.save(snapshot);
    }

    /**
     * Determine margin action based on current equity percentage.
     */
    private MarginAction determineAction(BigDecimal currentEquityPct,
                                         BigDecimal initialMarginPct,
                                         BigDecimal triggerMarginPct) {
        if (currentEquityPct.compareTo(initialMarginPct) >= 0) {
            return MarginAction.HEALTHY;
        } else if (currentEquityPct.compareTo(triggerMarginPct) >= 0) {
            return MarginAction.MARGIN_CALL;
        } else {
            return MarginAction.FORCE_SELL;
        }
    }

    /**
     * Calculate the shortfall amount – funds required to bring the account back to
     * maintenance margin level.
     *
     * <p>Shortfall = (maintenanceMarginPct / 100 * totalMarketValue) - currentEquity
     * Returns 0 when the account is HEALTHY.
     */
    private BigDecimal calculateShortfall(MarginAction action,
                                           BigDecimal currentEquity,
                                           BigDecimal totalMarketValue,
                                           BigDecimal maintenanceMarginPct) {
        if (action == MarginAction.HEALTHY) {
            return BigDecimal.ZERO;
        }
        BigDecimal requiredEquity = maintenanceMarginPct
                .divide(HUNDRED, 10, RoundingMode.HALF_UP)
                .multiply(totalMarketValue)
                .setScale(SCALE, RoundingMode.HALF_UP);

        BigDecimal shortfall = requiredEquity.subtract(currentEquity);
        return shortfall.max(BigDecimal.ZERO);
    }

    /**
     * Derive the limit status from days remaining before expiry.
     */
    private LimitStatus deriveLimitStatus(Long daysRemaining) {
        if (daysRemaining == null) {
            return null;
        }
        if (daysRemaining < 0) {
            return LimitStatus.EXPIRED;
        } else if (daysRemaining <= EXPIRING_SOON_DAYS) {
            return LimitStatus.EXPIRING_SOON;
        } else {
            return LimitStatus.ACTIVE;
        }
    }
}
