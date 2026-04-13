package com.margincalculator.controller;

import com.margincalculator.entity.ClientwiseInventory;
import com.margincalculator.entity.MarketData;
import com.margincalculator.entity.PledgeClientwiseInventory;
import com.margincalculator.service.InventoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing inventory and market data.
 *
 * <p>Base paths:
 * <ul>
 *   <li>/api/inventory      – clientwise purchased inventory</li>
 *   <li>/api/pledge         – clientwise pledged inventory</li>
 *   <li>/api/market-data    – market data (LTP, close price)</li>
 * </ul>
 */
@RestController
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    // -------------------------------------------------------------------------
    // Clientwise Inventory
    // -------------------------------------------------------------------------

    @GetMapping("/api/inventory")
    public ResponseEntity<List<ClientwiseInventory>> getAllInventory() {
        return ResponseEntity.ok(inventoryService.getAllInventory());
    }

    @GetMapping("/api/inventory/{clientId}")
    public ResponseEntity<List<ClientwiseInventory>> getInventoryByClient(@PathVariable String clientId) {
        return ResponseEntity.ok(inventoryService.getInventoryByClient(clientId));
    }

    @PostMapping("/api/inventory")
    public ResponseEntity<ClientwiseInventory> createInventory(@RequestBody ClientwiseInventory inventory) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.saveInventory(inventory));
    }

    @PutMapping("/api/inventory/{id}")
    public ResponseEntity<ClientwiseInventory> updateInventory(@PathVariable Long id,
                                                                @RequestBody ClientwiseInventory inventory) {
        inventory.setId(id);
        return ResponseEntity.ok(inventoryService.saveInventory(inventory));
    }

    @DeleteMapping("/api/inventory/{id}")
    public ResponseEntity<Void> deleteInventory(@PathVariable Long id) {
        inventoryService.deleteInventory(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Pledge Clientwise Inventory
    // -------------------------------------------------------------------------

    @GetMapping("/api/pledge")
    public ResponseEntity<List<PledgeClientwiseInventory>> getAllPledgeInventory() {
        return ResponseEntity.ok(inventoryService.getAllPledgeInventory());
    }

    @GetMapping("/api/pledge/{clientId}")
    public ResponseEntity<List<PledgeClientwiseInventory>> getPledgeInventoryByClient(@PathVariable String clientId) {
        return ResponseEntity.ok(inventoryService.getPledgeInventoryByClient(clientId));
    }

    @PostMapping("/api/pledge")
    public ResponseEntity<PledgeClientwiseInventory> createPledgeInventory(
            @RequestBody PledgeClientwiseInventory pledge) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.savePledgeInventory(pledge));
    }

    @PutMapping("/api/pledge/{id}")
    public ResponseEntity<PledgeClientwiseInventory> updatePledgeInventory(@PathVariable Long id,
                                                                             @RequestBody PledgeClientwiseInventory pledge) {
        pledge.setId(id);
        return ResponseEntity.ok(inventoryService.savePledgeInventory(pledge));
    }

    @DeleteMapping("/api/pledge/{id}")
    public ResponseEntity<Void> deletePledgeInventory(@PathVariable Long id) {
        inventoryService.deletePledgeInventory(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Market Data
    // -------------------------------------------------------------------------

    @GetMapping("/api/market-data")
    public ResponseEntity<List<MarketData>> getAllMarketData() {
        return ResponseEntity.ok(inventoryService.getAllMarketData());
    }

    @GetMapping("/api/market-data/symbol/{symbol}")
    public ResponseEntity<MarketData> getMarketDataBySymbol(@PathVariable String symbol) {
        return inventoryService.getMarketDataBySymbol(symbol)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/api/market-data")
    public ResponseEntity<MarketData> createMarketData(@RequestBody MarketData marketData) {
        return ResponseEntity.status(HttpStatus.CREATED).body(inventoryService.saveMarketData(marketData));
    }

    @PutMapping("/api/market-data/{id}")
    public ResponseEntity<MarketData> updateMarketData(@PathVariable Long id, @RequestBody MarketData marketData) {
        marketData.setId(id);
        return ResponseEntity.ok(inventoryService.saveMarketData(marketData));
    }

    @DeleteMapping("/api/market-data/{id}")
    public ResponseEntity<Void> deleteMarketData(@PathVariable Long id) {
        inventoryService.deleteMarketData(id);
        return ResponseEntity.noContent().build();
    }
}
