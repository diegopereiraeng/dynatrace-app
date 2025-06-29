package com.example.dynatraceapp.controller;

import org.springframework.beans.factory.annotation.Value;
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

    // Configuration for /payments endpoint latency
    @Value("${payments.latency.min.ms:50}")
    private int paymentsLatencyMinMs;

    @Value("${payments.latency.max.ms:200}")
    private int paymentsLatencyMaxMs;

    // Configuration for /cashout endpoint
    @Value("${cashout.failure.percentage:20}")
    private int cashoutFailurePercentage;
    @Value("${cashout.latency.min.ms:100}")
    private int cashoutLatencyMinMs;
    @Value("${cashout.latency.max.ms:300}")
    private int cashoutLatencyMaxMs;

    // Configuration for /credit-analysis endpoint
    @Value("${credit.analysis.latency.min.ms:1500}")
    private int creditAnalysisLatencyMinMs;
    @Value("${credit.analysis.latency.max.ms:3000}")
    private int creditAnalysisLatencyMaxMs;

    // Configuration for /loan-request endpoint
    @Value("${loan.request.failure.percentage:100}")
    private int loanRequestFailurePercentage;
    @Value("${loan.request.latency.min.ms:50}")
    private int loanRequestLatencyMinMs;
    @Value("${loan.request.latency.max.ms:150}")
    private int loanRequestLatencyMaxMs;


    private void simulateLatency(int minMs, int maxMs) throws InterruptedException {
        if (minMs <= 0 || maxMs <= 0 || minMs > maxMs) { // Basic validation
            Thread.sleep(ThreadLocalRandom.current().nextInt(50, 150)); // Default small latency
            return;
        }
        Thread.sleep(ThreadLocalRandom.current().nextInt(minMs, maxMs));
    }

    @GetMapping("/payments")
    public ResponseEntity<String> processPayment() {
        try {
            simulateLatency(paymentsLatencyMinMs, paymentsLatencyMaxMs);
            return ResponseEntity.ok("Payment processed successfully.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during payment processing: " + e.getMessage());
        }
    }

    @GetMapping("/cashout")
    public ResponseEntity<String> requestCashout() {
        try {
            simulateLatency(cashoutLatencyMinMs, cashoutLatencyMaxMs);

            if (random.nextInt(100) < cashoutFailurePercentage) {
                return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body("Cashout service temporarily unavailable (simulated error).");
            }
            return ResponseEntity.ok("Cashout request successful.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during cashout request: " + e.getMessage());
        }
    }

    @GetMapping("/credit-analysis")
    public ResponseEntity<String> performCreditAnalysis() {
        try {
            simulateLatency(creditAnalysisLatencyMinMs, creditAnalysisLatencyMaxMs);
            return ResponseEntity.ok("Credit analysis completed.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during credit analysis: " + e.getMessage());
        }
    }

    @GetMapping("/loan-request")
    public ResponseEntity<String> submitLoanRequest() {
        try {
            simulateLatency(loanRequestLatencyMinMs, loanRequestLatencyMaxMs);

            if (random.nextInt(100) < loanRequestFailurePercentage) {
                 throw new RuntimeException("Internal system error processing loan request (simulated error).");
            }
            return ResponseEntity.ok("Loan request submitted successfully.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Interrupted error during loan request: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Simulated error in /loan-request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process loan request due to an internal error (simulated).");
        }
    }
}
