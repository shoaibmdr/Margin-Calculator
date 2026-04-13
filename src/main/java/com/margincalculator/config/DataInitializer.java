package com.margincalculator.config;

import com.margincalculator.entity.*;
import com.margincalculator.repository.*;
import com.margincalculator.service.InventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Seeds the database with realistic sample data on startup.
 * Active only when the "seed" Spring profile is enabled, or always in the
 * default/dev profile (i.e. NOT in "test" or "prod").
 */
@Component
@Profile("!prod")
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final ClientRepository clientRepository;
    private final ClientwiseInventoryRepository clientwiseInventoryRepository;
    private final PledgeClientwiseInventoryRepository pledgeInventoryRepository;
    private final TradeLogRepository tradeLogRepository;
    private final MarketDataRepository marketDataRepository;
    private final InventoryService inventoryService;

    @Override
    public void run(String... args) {
        if (clientRepository.count() > 0) {
            log.info("Database already seeded – skipping data initialisation.");
            return;
        }

        log.info("Seeding sample data...");
        seedMarketData();
        seedClients();
        seedClientwiseInventory();
        seedPledgeInventory();
        seedTradeLogs();
        log.info("Sample data seeded successfully.");
    }

    private void seedMarketData() {
        marketDataRepository.save(new MarketData(null, "RELIANCE", "INE002A01018", bd("2950.75"), bd("2940.00")));
        marketDataRepository.save(new MarketData(null, "TCS",      "INE467B01029", bd("3820.50"), bd("3800.00")));
        marketDataRepository.save(new MarketData(null, "HDFCBANK", "INE040A01034", bd("1675.25"), bd("1660.00")));
        marketDataRepository.save(new MarketData(null, "INFY",     "INE009A01021", bd("1540.00"), bd("1530.00")));
        marketDataRepository.save(new MarketData(null, "WIPRO",    "INE075A01022", bd("475.30"),  bd("472.00")));
        marketDataRepository.save(new MarketData(null, "ICICIBANK","INE090A01021", bd("1125.60"), bd("1120.00")));
    }

    private void seedClients() {
        // HEALTHY client – equity well above initial margin
        clientRepository.save(new Client("C001", "Rajesh Kumar",
                bd("500000"), bd("50"), bd("40"), bd("30")));

        // MARGIN_CALL client – equity between trigger and initial margin
        clientRepository.save(new Client("C002", "Priya Sharma",
                bd("900000"), bd("50"), bd("40"), bd("30")));

        // FORCE_SELL client – equity below trigger margin
        clientRepository.save(new Client("C003", "Amit Singh",
                bd("800000"), bd("50"), bd("40"), bd("30")));
    }

    private void seedClientwiseInventory() {
        // C001 – purchased shares worth ~700,000
        clientwiseInventoryRepository.save(new ClientwiseInventory(null, "C001", "RELIANCE",  100, bd("295075.00")));
        clientwiseInventoryRepository.save(new ClientwiseInventory(null, "C001", "TCS",        50, bd("191025.00")));
        clientwiseInventoryRepository.save(new ClientwiseInventory(null, "C001", "HDFCBANK",  130, bd("217782.50")));

        // C002 – purchased shares worth ~450,000
        clientwiseInventoryRepository.save(new ClientwiseInventory(null, "C002", "INFY",      200, bd("308000.00")));
        clientwiseInventoryRepository.save(new ClientwiseInventory(null, "C002", "WIPRO",     300, bd("142590.00")));

        // C003 – purchased shares worth ~280,000
        clientwiseInventoryRepository.save(new ClientwiseInventory(null, "C003", "ICICIBANK", 150, bd("168840.00")));
        clientwiseInventoryRepository.save(new ClientwiseInventory(null, "C003", "RELIANCE",   40, bd("118030.00")));
    }

    private void seedPledgeInventory() {
        // C001 – pledged shares; auto-computed by InventoryService
        inventoryService.savePledgeInventory(
                new PledgeClientwiseInventory(null, "C001", "ICICIBANK", 100, bd("1125.60"), null, null));

        // C002 – pledged shares
        inventoryService.savePledgeInventory(
                new PledgeClientwiseInventory(null, "C002", "TCS", 30, bd("3820.50"), null, null));

        // C003 – pledged shares
        inventoryService.savePledgeInventory(
                new PledgeClientwiseInventory(null, "C003", "INFY", 50, bd("1540.00"), null, null));
    }

    private void seedTradeLogs() {
        LocalDate today = LocalDate.now();

        tradeLogRepository.save(new TradeLog(null, today.minusDays(5), "C001", "RELIANCE",  "BUY",  100,  100));
        tradeLogRepository.save(new TradeLog(null, today.minusDays(4), "C001", "TCS",       "BUY",   50,   50));
        tradeLogRepository.save(new TradeLog(null, today.minusDays(3), "C001", "HDFCBANK",  "BUY",  150,  150));
        tradeLogRepository.save(new TradeLog(null, today.minusDays(2), "C001", "HDFCBANK",  "SELL",  20,  -20));

        tradeLogRepository.save(new TradeLog(null, today.minusDays(5), "C002", "INFY",      "BUY",  200,  200));
        tradeLogRepository.save(new TradeLog(null, today.minusDays(3), "C002", "WIPRO",     "BUY",  300,  300));

        tradeLogRepository.save(new TradeLog(null, today.minusDays(5), "C003", "ICICIBANK", "BUY",  150,  150));
        tradeLogRepository.save(new TradeLog(null, today.minusDays(2), "C003", "RELIANCE",  "BUY",   40,   40));
    }

    private BigDecimal bd(String value) {
        return new BigDecimal(value);
    }
}
