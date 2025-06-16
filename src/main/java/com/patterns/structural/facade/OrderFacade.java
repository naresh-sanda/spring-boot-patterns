package com.patterns.structural.facade;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Facade Pattern - Provides a simplified interface to complex subsystems
 */
@Component
class InventoryService {
    public boolean checkStock(String productId) {
        System.out.println("Checking stock for product: " + productId);
        return true; // Simplified
    }
    
    public void reserveProduct(String productId, int quantity) {
        System.out.println("Reserved " + quantity + " units of " + productId);
    }
}

@Component
class PaymentService {
    public boolean processPayment(double amount) {
        System.out.println("Processing payment of $" + amount);
        return true; // Simplified
    }
}

@Component
class ShippingService {
    public void scheduleShipping(String address) {
        System.out.println("Shipping scheduled to: " + address);
    }
}

@Component
public class OrderFacade {
    
    @Autowired
    private InventoryService inventoryService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private ShippingService shippingService;
    
    public boolean placeOrder(String productId, int quantity, double amount, String address) {
        System.out.println("Starting order process...");
        
        // Check inventory
        if (!inventoryService.checkStock(productId)) {
            System.out.println("Product out of stock");
            return false;
        }
        
        // Reserve product
        inventoryService.reserveProduct(productId, quantity);
        
        // Process payment
        if (!paymentService.processPayment(amount)) {
            System.out.println("Payment failed");
            return false;
        }
        
        // Schedule shipping
        shippingService.scheduleShipping(address);
        
        System.out.println("Order placed successfully!");
        return true;
    }
}
