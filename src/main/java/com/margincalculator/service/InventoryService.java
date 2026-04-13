package com.margincalculator.service;

import com.margincalculator.entity.ClientwiseInventory;
import com.margincalculator.entity.MarketData;
import com.margincalculator.entity.PledgeClientwiseInventory;
import com.margincalculator.repository.ClientwiseInventoryRepository;
import com.margincalculator.repository.MarketDataRepository;
import com.margincalculator.repository.PledgeClientwiseInventoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;

/**
 * Service for managing inventory and market data, including automatic
 * recalculation of market values when LTP changes.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class InventoryService {

    private static final BigDecimal PLEDGE_PCT = new BigDecimal("0.60");
    private static final int SCALE = 2;

    private final ClientwiseInventoryRepository clientwiseInventoryRepository;
    private final PledgeClientwiseInventoryRepository pledgeInventoryRepository;
    private final MarketDataRepository marketDataRepository;

    // -------------------------------------------------------------------------
    // Clientwise Inventory
    // -------------------------------------------------------------------------

    public List<ClientwiseInventory> getAllInventory() {
        return clientwiseInventoryRepository.findAll();
    }

    public List<ClientwiseInventory> getInventoryByClient(String clientId) {
        return clientwiseInventoryRepository.findByClientId(clientId);
    }

    public ClientwiseInventory saveInventory(ClientwiseInventory inventory) {
        return clientwiseInventoryRepository.save(inventory);
    }

    public void deleteInventory(Long id) {
        clientwiseInventoryRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Pledge Clientwise Inventory
    // -------------------------------------------------------------------------

    public List<PledgeClientwiseInventory> getAllPledgeInventory() {
        return pledgeInventoryRepository.findAll();
    }

    public List<PledgeClientwiseInventory> getPledgeInventoryByClient(String clientId) {
        return pledgeInventoryRepository.findByClientId(clientId);
    }

    /**
     * Save a pledge inventory entry and auto-compute marketValue and 60% value
     * from the provided LTP and quantity.
     */
    public PledgeClientwiseInventory savePledgeInventory(PledgeClientwiseInventory pledge) {
        BigDecimal mv = pledge.getLtp()
                .multiply(BigDecimal.valueOf(pledge.getTotalQuantity()))
                .setScale(SCALE, RoundingMode.HALF_UP);
        pledge.setMarketValue(mv);
        pledge.setMarketValue60Pct(mv.multiply(PLEDGE_PCT).setScale(SCALE, RoundingMode.HALF_UP));
        return pledgeInventoryRepository.save(pledge);
    }

    public void deletePledgeInventory(Long id) {
        pledgeInventoryRepository.deleteById(id);
    }

    // -------------------------------------------------------------------------
    // Market Data
    // -------------------------------------------------------------------------

    public List<MarketData> getAllMarketData() {
        return marketDataRepository.findAll();
    }

    public Optional<MarketData> getMarketDataBySymbol(String symbol) {
        return marketDataRepository.findBySymbol(symbol);
    }

    public MarketData saveMarketData(MarketData marketData) {
        return marketDataRepository.save(marketData);
    }

    public void deleteMarketData(Long id) {
        marketDataRepository.deleteById(id);
    }
}
