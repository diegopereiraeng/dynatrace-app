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
    @Value("${cashout.error.5xx.code:503}")
    private int cashout5xxErrorCode;
    @Value("${cashout.error.4xx.code:400}")
    private int cashout4xxErrorCode;
    @Value("${cashout.percentage.of.failures.as.5xx:70}")
    private int cashoutPercentageOfFailuresAs5xx;


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
    @Value("${loan.request.error.5xx.code:500}")
    private int loanRequest5xxErrorCode;
    @Value("${loan.request.error.4xx.code:400}")
    private int loanRequest4xxErrorCode;
    @Value("${loan.request.percentage.of.failures.as.5xx:100}")
    private int loanRequestPercentageOfFailuresAs5xx;


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
                // Determine if it's a 5xx or 4xx error based on weight
                if (random.nextInt(100) < cashoutPercentageOfFailuresAs5xx) {
                    // Return configured 5xx error
                    return ResponseEntity.status(HttpStatus.valueOf(cashout5xxErrorCode))
                                         .body("Cashout service temporarily unavailable (simulated 5xx error: " + cashout5xxErrorCode + ").");
                } else {
                    // Return configured 4xx error
                    return ResponseEntity.status(HttpStatus.valueOf(cashout4xxErrorCode))
                                         .body("Invalid cashout request (simulated 4xx error: " + cashout4xxErrorCode + ").");
                }
            }
            return ResponseEntity.ok("Cashout request successful.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error during cashout request: " + e.getMessage());
        } catch (RuntimeException e) { // Catch potential issues from HttpStatus.valueOf or other runtime issues
            System.err.println("Error processing /cashout: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Internal error processing cashout request.");
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
                // Determine if it's a 5xx or 4xx error based on weight
                if (random.nextInt(100) < loanRequestPercentageOfFailuresAs5xx) {
                    // Throw a RuntimeException which will be caught and result in a 500, or use specific 5xx code
                    if (loanRequest5xxErrorCode == 500) { // Default behavior: throw exception for 500
                        throw new RuntimeException("Internal system error processing loan request (simulated 5xx error: " + loanRequest5xxErrorCode + ").");
                    } else {
                        // Return specific configured 5xx error
                        return ResponseEntity.status(HttpStatus.valueOf(loanRequest5xxErrorCode))
                                             .body("Failed to process loan request due to a server-side issue (simulated 5xx error: " + loanRequest5xxErrorCode + ").");
                    }
                } else {
                    // Return configured 4xx error
                    return ResponseEntity.status(HttpStatus.valueOf(loanRequest4xxErrorCode))
                                         .body("Invalid loan request (simulated 4xx error: " + loanRequest4xxErrorCode + ").");
                }
            }
            return ResponseEntity.ok("Loan request submitted successfully.");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Interrupted error during loan request: " + e.getMessage());
        } catch (RuntimeException e) { // This will catch the RuntimeException thrown for 500-type errors
            System.err.println("Simulated runtime error in /loan-request: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR) // Default to 500 if exception is caught
                                 .body("Failed to process loan request due to an internal error (simulated). Detail: " + e.getMessage());
        // Removed redundant catch for IllegalArgumentException as RuntimeException already covers it.
        }
    }
}
