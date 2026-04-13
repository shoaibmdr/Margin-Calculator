package com.margincalculator.service;

import com.margincalculator.entity.Client;
import com.margincalculator.entity.TradeLog;
import com.margincalculator.repository.ClientRepository;
import com.margincalculator.repository.TradeLogRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Service for managing clients and trade log entries.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class ClientService {

    private final ClientRepository clientRepository;
    private final TradeLogRepository tradeLogRepository;

    // -------------------------------------------------------------------------
    // Client CRUD
    // -------------------------------------------------------------------------

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }

    public Optional<Client> getClientById(String clientId) {
        return clientRepository.findById(clientId);
    }

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public void deleteClient(String clientId) {
        clientRepository.deleteById(clientId);
    }

    // -------------------------------------------------------------------------
    // Trade Log CRUD
    // -------------------------------------------------------------------------

    public List<TradeLog> getAllTrades() {
        return tradeLogRepository.findAll();
    }

    public List<TradeLog> getTradesByClient(String clientId) {
        return tradeLogRepository.findByClientId(clientId);
    }

    public TradeLog saveTrade(TradeLog tradeLog) {
        return tradeLogRepository.save(tradeLog);
    }

    public void deleteTrade(Long id) {
        tradeLogRepository.deleteById(id);
    }
}
