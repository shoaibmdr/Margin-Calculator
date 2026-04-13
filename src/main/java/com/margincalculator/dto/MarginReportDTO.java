package com.margincalculator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Margin report for a single client.
 *
 * <p>Summary columns:
 * <ul>
 *   <li>clientId</li>
 *   <li>clientName</li>
 *   <li>marketValuePurchasedShares  – total market value of bought/held inventory</li>
 *   <li>marketValuePledgedShares60Pct – 60% collateral value of pledged inventory</li>
 *   <li>totalMarketValue            – sum of the two above</li>
 *   <li>loanOutstanding             – loan / client receivable</li>
 *   <li>currentEquity               – totalMarketValue - loanOutstanding</li>
 *   <li>currentEquityPct            – (currentEquity / totalMarketValue) * 100</li>
 *   <li>initialMarginPct            – threshold for HEALTHY status</li>
 *   <li>maintenanceMarginPct        – threshold below which MARGIN_CALL is triggered</li>
 *   <li>triggerMarginPct            – threshold below which FORCE_SELL is triggered</li>
 *   <li>shortfallAmount             – funds needed to reach maintenance margin (0 if HEALTHY)</li>
 *   <li>action                      – HEALTHY | MARGIN_CALL | FORCE_SELL</li>
 *   <li>clientPan                   – client PAN number</li>
 *   <li>clientMobileNumber          – client mobile number</li>
 *   <li>approvedMarginTradingLimit  – sanctioned margin trading limit</li>
 *   <li>approvedLoanLimit           – sanctioned loan limit</li>
 *   <li>limitApprovedDate           – date when limit was approved</li>
 *   <li>limitExpiryDate             – date when limit expires</li>
 *   <li>daysRemainingBeforeExpiry   – days until expiry (negative if expired)</li>
 *   <li>limitStatus                 – ACTIVE | EXPIRING_SOON | EXPIRED</li>
 * </ul>
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MarginReportDTO {

    private String clientId;
    private String clientName;

    private BigDecimal marketValuePurchasedShares;
    private BigDecimal marketValuePledgedShares60Pct;
    private BigDecimal totalMarketValue;

    private BigDecimal loanOutstanding;
    private BigDecimal currentEquity;
    private BigDecimal currentEquityPct;

    private BigDecimal initialMarginPct;
    private BigDecimal maintenanceMarginPct;
    private BigDecimal triggerMarginPct;

    private BigDecimal shortfallAmount;
    private MarginAction action;

    private String clientPan;
    private String clientMobileNumber;

    private BigDecimal approvedMarginTradingLimit;
    private BigDecimal approvedLoanLimit;
    private LocalDate limitApprovedDate;
    private LocalDate limitExpiryDate;
    private Long daysRemainingBeforeExpiry;
    private LimitStatus limitStatus;
}
