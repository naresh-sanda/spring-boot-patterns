package com.patterns.structural.adapter;

import org.springframework.stereotype.Component;

/**
 * Adapter Pattern - Allows incompatible interfaces to work together
 */
// Legacy payment system
class LegacyPaymentSystem {
    public void makePayment(double amount) {
        System.out.println("Legacy payment of $" + amount + " processed");
    }
}

// Modern payment interface
interface ModernPaymentProcessor {
    void processPayment(String amount, String currency);
}

@Component
public class PaymentAdapter implements ModernPaymentProcessor {
    private LegacyPaymentSystem legacySystem;
    
    public PaymentAdapter() {
        this.legacySystem = new LegacyPaymentSystem();
    }
    
    @Override
    public void processPayment(String amount, String currency) {
        // Convert modern interface to legacy interface
        double numericAmount = Double.parseDouble(amount);
        System.out.println("Adapting payment for currency: " + currency);
        legacySystem.makePayment(numericAmount);
    }
}
