package com.example.dynatraceapp.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

@RestController
@RequestMapping("/api")
public class ApiController {

    private final Random random = new Random();

    // Simula uma requisição de processamento de pagamento bem-sucedida.
    @GetMapping("/payments")
    public ResponseEntity<String> processPayment() {
        try {
            // Simula algum processamento
            Thread.sleep(ThreadLocalRandom.current().nextInt(50, 200));
            return ResponseEntity.ok("Payment processed successfully.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during payment processing.");
        }
    }

    // Simula uma requisição de saque que ocasionalmente falha (flaky).
    @GetMapping("/cashout")
    public ResponseEntity<String> requestCashout() {
        try {
            // Simula algum processamento
            Thread.sleep(ThreadLocalRandom.current().nextInt(100, 300));

            // 20% de chance de falha
            if (random.nextInt(100) < 20) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Cashout service temporarily unavailable.");
            }
            return ResponseEntity.ok("Cashout request successful.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during cashout request.");
        }
    }

    // Simula uma requisição de análise de crédito com alta latência.
    @GetMapping("/credit-analysis")
    public ResponseEntity<String> performCreditAnalysis() {
        try {
            // Simula processamento demorado
            Thread.sleep(ThreadLocalRandom.current().nextInt(1500, 3000)); // Latência entre 1.5s e 3s
            return ResponseEntity.ok("Credit analysis completed.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during credit analysis.");
        }
    }

    // Simula uma requisição de empréstimo que resulta em erro interno.
    @GetMapping("/loan-request")
    public ResponseEntity<String> submitLoanRequest() {
        try {
            // Simula algum processamento
            Thread.sleep(ThreadLocalRandom.current().nextInt(50, 150));
            // Força um erro
            if (true) { // Sempre causa erro para este endpoint
                 throw new RuntimeException("Internal system error processing loan request.");
            }
            return ResponseEntity.ok("Loan request submitted (this should not be reached).");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Interrupted error during loan request.");
        } catch (RuntimeException e) {
            // Log do erro (em um cenário real, use um logger)
            System.err.println("Error in /loan-request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process loan request due to an internal error.");
        }
    }
}
