package com.patterns.behavioral.strategy;

import org.springframework.stereotype.Component;
import java.util.Map;
import java.util.HashMap;

/**
 * Strategy Pattern - Defines family of algorithms and makes them interchangeable
 */
interface PaymentStrategy {
    void pay(double amount);
}

@Component
class CreditCardPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using Credit Card");
    }
}

@Component
class PayPalPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using PayPal");
    }
}

@Component
class BankTransferPayment implements PaymentStrategy {
    @Override
    public void pay(double amount) {
        System.out.println("Paid $" + amount + " using Bank Transfer");
    }
}

@Component
public class PaymentContext {
    private Map<String, PaymentStrategy> strategies;
    
    public PaymentContext() {
        strategies = new HashMap<>();
        strategies.put("credit", new CreditCardPayment());
        strategies.put("paypal", new PayPalPayment());
        strategies.put("bank", new BankTransferPayment());
    }
    
    public void executePayment(String paymentType, double amount) {
        PaymentStrategy strategy = strategies.get(paymentType.toLowerCase());
        if (strategy != null) {
            strategy.pay(amount);
        } else {
            System.out.println("Payment method not supported: " + paymentType);
        }
    }
}
