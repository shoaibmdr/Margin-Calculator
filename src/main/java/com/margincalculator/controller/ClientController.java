package com.margincalculator.controller;

import com.margincalculator.entity.Client;
import com.margincalculator.entity.TradeLog;
import com.margincalculator.service.ClientService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing clients and trade log.
 *
 * <p>Base path: /api/clients
 */
@RestController
@RequestMapping("/api/clients")
@RequiredArgsConstructor
public class ClientController {

    private final ClientService clientService;

    // -------------------------------------------------------------------------
    // Client endpoints
    // -------------------------------------------------------------------------

    @GetMapping
    public ResponseEntity<List<Client>> getAllClients() {
        return ResponseEntity.ok(clientService.getAllClients());
    }

    @GetMapping("/{clientId}")
    public ResponseEntity<Client> getClient(@PathVariable String clientId) {
        return clientService.getClientById(clientId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<Client> createClient(@RequestBody Client client) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.saveClient(client));
    }

    @PutMapping("/{clientId}")
    public ResponseEntity<Client> updateClient(@PathVariable String clientId, @RequestBody Client client) {
        client.setClientId(clientId);
        return ResponseEntity.ok(clientService.saveClient(client));
    }

    @DeleteMapping("/{clientId}")
    public ResponseEntity<Void> deleteClient(@PathVariable String clientId) {
        clientService.deleteClient(clientId);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // Trade Log endpoints
    // -------------------------------------------------------------------------

    @GetMapping("/trades")
    public ResponseEntity<List<TradeLog>> getAllTrades() {
        return ResponseEntity.ok(clientService.getAllTrades());
    }

    @GetMapping("/{clientId}/trades")
    public ResponseEntity<List<TradeLog>> getTradesByClient(@PathVariable String clientId) {
        return ResponseEntity.ok(clientService.getTradesByClient(clientId));
    }

    @PostMapping("/trades")
    public ResponseEntity<TradeLog> createTrade(@RequestBody TradeLog tradeLog) {
        return ResponseEntity.status(HttpStatus.CREATED).body(clientService.saveTrade(tradeLog));
    }

    @DeleteMapping("/trades/{id}")
    public ResponseEntity<Void> deleteTrade(@PathVariable Long id) {
        clientService.deleteTrade(id);
        return ResponseEntity.noContent().build();
    }
}
