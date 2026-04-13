package com.margincalculator.controller;

import com.margincalculator.dto.MarginReportDTO;
import com.margincalculator.service.MarginCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing margin report endpoints.
 *
 * <p>Base path: /api/margin
 */
@RestController
@RequestMapping("/api/margin")
@RequiredArgsConstructor
public class MarginController {

    private final MarginCalculationService marginCalculationService;

    /**
     * GET /api/margin/report
     * Returns the margin report for all clients showing:
     * clientId, clientName, marketValuePurchasedShares,
     * marketValuePledgedShares60Pct, totalMarketValue,
     * loanOutstanding, currentEquity, currentEquityPct,
     * thresholds, shortfallAmount, action.
     */
    @GetMapping("/report")
    public ResponseEntity<List<MarginReportDTO>> getAllClientsReport() {
        return ResponseEntity.ok(marginCalculationService.generateReportForAllClients());
    }

    /**
     * GET /api/margin/report/{clientId}
     * Returns the margin report for a single client.
     */
    @GetMapping("/report/{clientId}")
    public ResponseEntity<MarginReportDTO> getClientReport(@PathVariable String clientId) {
        return ResponseEntity.ok(marginCalculationService.generateReportForClient(clientId));
    }
}
