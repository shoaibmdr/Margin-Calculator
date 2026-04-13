package com.margincalculator.controller;

import com.margincalculator.dto.MarginReportDTO;
import com.margincalculator.entity.MarginMonitoringSnapshot;
import com.margincalculator.service.MarginCalculationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller exposing margin report and monitoring endpoints.
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
     * Returns the margin report for all clients and refreshes the monitoring table.
     */
    @GetMapping("/report")
    public ResponseEntity<List<MarginReportDTO>> getAllClientsReport() {
        return ResponseEntity.ok(marginCalculationService.generateReportForAllClients());
    }

    /**
     * GET /api/margin/report/{clientId}
     * Returns the margin report for a single client and refreshes its monitoring row.
     */
    @GetMapping("/report/{clientId}")
    public ResponseEntity<MarginReportDTO> getClientReport(@PathVariable String clientId) {
        return ResponseEntity.ok(marginCalculationService.generateReportForClient(clientId));
    }

    /**
     * GET /api/margin/monitoring
     * Returns the main monitoring table (margin_monitoring_snapshot) reflecting
     * the state as of the last margin calculation run, including the last-modified date
     * of each row and all columns from the monitoring format:
     * SN, Client ID, Client Name, Market Value of Purchased Shares,
     * Market Value of Pledged Shares (60%), Total Market Value,
     * Loan Outstanding/Current Note Receivable, Current Equity (NPR),
     * Current Margin %, Initial Margin %, Maint. Margin %, Trigger Margin %,
     * Status/Action, Shortfall Amount, Client PAN, Client Mobile Number,
     * Approved Margin Trading Limit, Approved Loan Limit,
     * Limit Approved Date, Limit Expiry Date, Days Remaining Before Expiry,
     * Limit Status, Last Modified Date.
     */
    @GetMapping("/monitoring")
    public ResponseEntity<List<MarginMonitoringSnapshot>> getMonitoringTable() {
        return ResponseEntity.ok(marginCalculationService.getMonitoringTable());
    }
}
