package com.patterns.behavioral.observer;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Observer Pattern - Notifies multiple objects about state changes
 * Using Spring's event mechanism
 */
class OrderCreatedEvent {
    private String orderId;
    private double amount;
    
    public OrderCreatedEvent(String orderId, double amount) {
        this.orderId = orderId;
        this.amount = amount;
    }
    
    public String getOrderId() { return orderId; }
    public double getAmount() { return amount; }
}

@Component
public class OrderEventPublisher {
    
    @Autowired
    private ApplicationEventPublisher eventPublisher;
    
    public void createOrder(String orderId, double amount) {
        System.out.println("Creating order: " + orderId);
        
        // Publish event
        eventPublisher.publishEvent(new OrderCreatedEvent(orderId, amount));
    }
}

@Component
class EmailNotificationListener {
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("Email notification: Order " + event.getOrderId() + 
                          " created with amount $" + event.getAmount());
    }
}

@Component
class InventoryUpdateListener {
    
    @EventListener
    public void handleOrderCreated(OrderCreatedEvent event) {
        System.out.println("Inventory update: Processing order " + event.getOrderId());
    }
}
