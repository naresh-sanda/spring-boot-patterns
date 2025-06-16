package com.patterns.behavioral.state;

import org.springframework.stereotype.Component;

/**
 * State Pattern - Changes object behavior based on internal state
 */
interface OrderState {
    void next(OrderContext context);
    void prev(OrderContext context);
    void printStatus();
}

class PendingState implements OrderState {
    @Override
    public void next(OrderContext context) {
        context.setState(new ConfirmedState());
    }
    
    @Override
    public void prev(OrderContext context) {
        System.out.println("Order is in initial state");
    }
    
    @Override
    public void printStatus() {
        System.out.println("Order Status: PENDING");
    }
}

class ConfirmedState implements OrderState {
    @Override
    public void next(OrderContext context) {
        context.setState(new ShippedState());
    }
    
    @Override
    public void prev(OrderContext context) {
        context.setState(new PendingState());
    }
    
    @Override
    public void printStatus() {
        System.out.println("Order Status: CONFIRMED");
    }
}

class ShippedState implements OrderState {
    @Override
    public void next(OrderContext context) {
        context.setState(new DeliveredState());
    }
    
    @Override
    public void prev(OrderContext context) {
        context.setState(new ConfirmedState());
    }
    
    @Override
    public void printStatus() {
        System.out.println("Order Status: SHIPPED");
    }
}

class DeliveredState implements OrderState {
    @Override
    public void next(OrderContext context) {
        System.out.println("Order is in final state");
    }
    
    @Override
    public void prev(OrderContext context) {
        context.setState(new ShippedState());
    }
    
    @Override
    public void printStatus() {
        System.out.println("Order Status: DELIVERED");
    }
}

@Component
public class OrderContext {
    private OrderState state;
    
    public OrderContext() {
        state = new PendingState();
    }
    
    public void setState(OrderState state) {
        this.state = state;
    }
    
    public void nextState() {
        state.next(this);
    }
    
    public void prevState() {
        state.prev(this);
    }
    
    public void printStatus() {
        state.printStatus();
    }
}
