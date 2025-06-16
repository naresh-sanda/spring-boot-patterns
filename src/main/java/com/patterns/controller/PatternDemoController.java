package com.patterns.controller;

import com.patterns.creational.singleton.DatabaseConnection;
import com.patterns.creational.factory.NotificationFactory;
import com.patterns.creational.builder.User;
import com.patterns.structural.adapter.PaymentAdapter;
import com.patterns.structural.decorator.CoffeeService;
import com.patterns.structural.facade.OrderFacade;
import com.patterns.behavioral.observer.OrderEventPublisher;
import com.patterns.behavioral.strategy.PaymentContext;
import com.patterns.behavioral.command.RemoteControl;
import com.patterns.behavioral.command.Light;
import com.patterns.behavioral.command.LightOnCommand;
import com.patterns.behavioral.template.CSVDataProcessor;
import com.patterns.behavioral.state.OrderContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/patterns")
public class PatternDemoController {
    
    @Autowired
    private DatabaseConnection databaseConnection;
    
    @Autowired
    private NotificationFactory notificationFactory;
    
    @Autowired
    private PaymentAdapter paymentAdapter;
    
    @Autowired
    private CoffeeService coffeeService;
    
    @Autowired
    private OrderFacade orderFacade;
    
    @Autowired
    private OrderEventPublisher orderEventPublisher;
    
    @Autowired
    private PaymentContext paymentContext;
    
    @Autowired
    private RemoteControl remoteControl;
    
    @Autowired
    private CSVDataProcessor csvDataProcessor;
    
    @Autowired
    private OrderContext orderContext;
    
    @GetMapping("/singleton")
    public Map<String, Object> testSingleton() {
        Map<String, Object> response = new HashMap<>();
        databaseConnection.connect();
        response.put("connected", databaseConnection.isConnected());
        response.put("pattern", "Singleton Pattern - Single instance managed by Spring");
        return response;
    }
    
    @GetMapping("/factory/{type}")
    public Map<String, Object> testFactory(@PathVariable String type) {
        Map<String, Object> response = new HashMap<>();
        try {
            var notification = notificationFactory.createNotification(type);
            notification.send("Test message");
            response.put("success", true);
            response.put("type", type);
            response.put("pattern", "Factory Pattern - Creates objects without specifying exact classes");
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", e.getMessage());
        }
        return response;
    }
    
    @GetMapping("/builder")
    public Map<String, Object> testBuilder() {
        Map<String, Object> response = new HashMap<>();
        
        User user = new User.UserBuilder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .age(30)
                .build();
        
        response.put("user", user.toString());
        response.put("pattern", "Builder Pattern - Constructs complex objects step by step");
        return response;
    }
    
    @PostMapping("/adapter/payment")
    public Map<String, Object> testAdapter(@RequestBody Map<String, String> request) {
        Map<String, Object> response = new HashMap<>();
        
        String amount = request.get("amount");
        String currency = request.get("currency");
        
        paymentAdapter.processPayment(amount, currency);
        
        response.put("success", true);
        response.put("pattern", "Adapter Pattern - Allows incompatible interfaces to work together");
        return response;
    }
    
    @GetMapping("/decorator/coffee")
    public Map<String, Object> testDecorator() {
        Map<String, Object> response = new HashMap<>();
        
        var coffee = coffeeService.createCoffeeWithMilkAndSugar();
        
        response.put("description", coffee.getDescription());
        response.put("cost", coffee.getCost());
        response.put("pattern", "Decorator Pattern - Adds behavior to objects dynamically");
        return response;
    }
    
    @PostMapping("/facade/order")
    public Map<String, Object> testFacade(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        String productId = (String) request.get("productId");
        Integer quantity = (Integer) request.get("quantity");
        Double amount = (Double) request.get("amount");
        String address = (String) request.get("address");
        
        boolean success = orderFacade.placeOrder(productId, quantity, amount, address);
        
        response.put("success", success);
        response.put("pattern", "Facade Pattern - Provides simplified interface to complex subsystems");
        return response;
    }
    
    @PostMapping("/observer/order")
    public Map<String, Object> testObserver(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        String orderId = (String) request.get("orderId");
        Double amount = (Double) request.get("amount");
        
        orderEventPublisher.createOrder(orderId, amount);
        
        response.put("success", true);
        response.put("pattern", "Observer Pattern - Notifies multiple objects about state changes");
        return response;
    }
    
    @PostMapping("/strategy/payment")
    public Map<String, Object> testStrategy(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = new HashMap<>();
        
        String paymentType = (String) request.get("paymentType");
        Double amount = (Double) request.get("amount");
        
        paymentContext.executePayment(paymentType, amount);
        
        response.put("success", true);
        response.put("pattern", "Strategy Pattern - Defines family of algorithms and makes them interchangeable");
        return response;
    }
    
    @PostMapping("/command/light/{action}")
    public Map<String, Object> testCommand(@PathVariable String action) {
        Map<String, Object> response = new HashMap<>();
        
        Light light = new Light();
        
        if ("on".equals(action)) {
            remoteControl.executeCommand(new LightOnCommand(light));
        } else if ("undo".equals(action)) {
            remoteControl.undoLastCommand();
        }
        
        response.put("success", true);
        response.put("action", action);
        response.put("pattern", "Command Pattern - Encapsulates requests as objects");
        return response;
    }
    
    @GetMapping("/template/csv")
    public Map<String, Object> testTemplate() {
        Map<String, Object> response = new HashMap<>();
        
        csvDataProcessor.processData();
        
        response.put("success", true);
        response.put("pattern", "Template Method Pattern - Defines skeleton of algorithm in base class");
        return response;
    }
    
    @PostMapping("/state/order/{action}")
    public Map<String, Object> testState(@PathVariable String action) {
        Map<String, Object> response = new HashMap<>();
        
        if ("next".equals(action)) {
            orderContext.nextState();
        } else if ("prev".equals(action)) {
            orderContext.prevState();
        }
        
        orderContext.printStatus();
        
        response.put("success", true);
        response.put("action", action);
        response.put("pattern", "State Pattern - Changes object behavior based on internal state");
        return response;
    }
    
    @GetMapping("/all")
    public Map<String, Object> getAllPatterns() {
        Map<String, Object> response = new HashMap<>();
        
        response.put("creational", new String[]{
            "Singleton - Single instance management",
            "Factory - Object creation without specifying exact classes", 
            "Builder - Step-by-step object construction"
        });
        
        response.put("structural", new String[]{
            "Adapter - Interface compatibility",
            "Decorator - Dynamic behavior addition",
            "Facade - Simplified interface to complex subsystems"
        });
        
        response.put("behavioral", new String[]{
            "Observer - State change notifications",
            "Strategy - Interchangeable algorithms",
            "Command - Request encapsulation",
            "Template Method - Algorithm skeleton definition",
            "State - Behavior changes based on state"
        });
        
        return response;
    }
}
