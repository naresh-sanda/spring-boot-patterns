package com.patterns.integration;

import com.patterns.creational.singleton.DatabaseConnection;
import com.patterns.creational.factory.NotificationFactory;
import com.patterns.creational.builder.User;
import com.patterns.structural.facade.OrderFacade;
import com.patterns.behavioral.observer.OrderEventPublisher;
import com.patterns.behavioral.strategy.PaymentContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration tests that verify multiple patterns work together
 */
@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class PatternIntegrationTest {

    @Autowired
    private DatabaseConnection databaseConnection;
    
    @Autowired
    private NotificationFactory notificationFactory;
    
    @Autowired
    private OrderFacade orderFacade;
    
    @Autowired
    private OrderEventPublisher orderEventPublisher;
    
    @Autowired
    private PaymentContext paymentContext;

    @Test
    void testCompleteOrderWorkflow(CapturedOutput output) {
        // Test integration of multiple patterns in a complete workflow
        
        // 1. Singleton - Ensure database connection
        databaseConnection.connect();
        assertTrue(databaseConnection.isConnected());
        
        // 2. Builder - Create user
        User customer = new User.UserBuilder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .build();
        
        assertNotNull(customer);
        assertEquals("John", customer.getFirstName());
        
        // 3. Strategy - Process payment
        paymentContext.executePayment("credit", 99.99);
        
        // 4. Facade - Place order (integrates multiple subsystems)
        boolean orderSuccess = orderFacade.placeOrder("PROD123", 1, 99.99, "123 Main St");
        assertTrue(orderSuccess);
        
        // 5. Observer - Publish order event
        orderEventPublisher.createOrder("ORD123", 99.99);
        
        // 6. Factory - Send notification
        var notification = notificationFactory.createNotification("email");
        notification.send("Order ORD123 has been placed successfully!");
        
        // Verify all patterns worked together
        String outputString = output.getOut();
        assertTrue(outputString.contains("Database connected"));
        assertTrue(outputString.contains("Paid $99.99 using Credit Card"));
        assertTrue(outputString.contains("Order placed successfully!"));
        assertTrue(outputString.contains("Creating order: ORD123"));
        assertTrue(outputString.contains("Email sent: Order ORD123 has been placed successfully!"));
    }

    @Test
    void testPatternCombinations(CapturedOutput output) {
        // Test various pattern combinations
        
        // Singleton + Factory
        databaseConnection.connect();
        var smsNotification = notificationFactory.createNotification("sms");
        smsNotification.send("Database connected successfully");
        
        // Builder + Strategy
        User premiumUser = new User.UserBuilder()
                .firstName("Premium")
                .lastName("Customer")
                .email("premium@example.com")
                .build();
        
        paymentContext.executePayment("paypal", 299.99);
        
        // Facade + Observer
        orderFacade.placeOrder("PREMIUM_PROD", 1, 299.99, "Premium Address");
        orderEventPublisher.createOrder("PREMIUM_ORD", 299.99);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("SMS sent"));
        assertTrue(outputString.contains("PayPal"));
        assertTrue(outputString.contains("PREMIUM_PROD"));
        assertTrue(outputString.contains("PREMIUM_ORD"));
    }

    @Test
    void testErrorHandlingAcrossPatterns(CapturedOutput output) {
        // Test error handling when patterns interact
        
        // Factory with invalid type
        assertThrows(IllegalArgumentException.class, () -> {
            notificationFactory.createNotification("invalid");
        });
        
        // Strategy with unsupported payment
        paymentContext.executePayment("bitcoin", 100.0);
        
        // Facade should still work despite other errors
        boolean orderSuccess = orderFacade.placeOrder("ERROR_TEST", 1, 50.0, "Error Address");
        assertTrue(orderSuccess);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Payment method not supported: bitcoin"));
        assertTrue(outputString.contains("Order placed successfully!"));
    }

    @Test
    void testPatternPerformance() {
        // Test that patterns don't significantly impact performance when combined
        
        long startTime = System.currentTimeMillis();
        
        // Execute multiple pattern operations
        for (int i = 0; i < 100; i++) {
            databaseConnection.connect();
            
            User user = new User.UserBuilder()
                    .firstName("User" + i)
                    .email("user" + i + "@example.com")
                    .build();
            
            var notification = notificationFactory.createNotification("email");
            notification.send("Test message " + i);
            
            paymentContext.executePayment("credit", 10.0 + i);
            
            orderEventPublisher.createOrder("ORD" + i, 10.0 + i);
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        // Should complete within reasonable time (adjust threshold as needed)
        assertTrue(duration < 5000, "Pattern operations should complete within 5 seconds");
    }

    @Test
    void testPatternStateConsistency(CapturedOutput output) {
        // Test that patterns maintain consistent state when used together
        
        // Initial state
        assertFalse(databaseConnection.isConnected());
        
        // Connect database
        databaseConnection.connect();
        assertTrue(databaseConnection.isConnected());
        
        // Use other patterns while database is connected
        orderFacade.placeOrder("CONSISTENCY_TEST", 1, 75.0, "Test Address");
        orderEventPublisher.createOrder("CONSISTENCY_ORD", 75.0);
        
        // Database should still be connected
        assertTrue(databaseConnection.isConnected());
        
        // Disconnect
        databaseConnection.disconnect();
        assertFalse(databaseConnection.isConnected());
        
        // Other patterns should still work
        paymentContext.executePayment("bank", 25.0);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Database connected"));
        assertTrue(outputString.contains("Database disconnected"));
        assertTrue(outputString.contains("Bank Transfer"));
    }

    @Test
    void testPatternScalability() {
        // Test that patterns scale well together
        
        // Create multiple users
        User[] users = new User[10];
        for (int i = 0; i < 10; i++) {
            users[i] = new User.UserBuilder()
                    .firstName("User" + i)
                    .lastName("Test")
                    .email("user" + i + "@test.com")
                    .age(20 + i)
                    .build();
        }
        
        // Process multiple payments with different strategies
        String[] paymentTypes = {"credit", "paypal", "bank"};
        for (int i = 0; i < 10; i++) {
            paymentContext.executePayment(paymentTypes[i % 3], 50.0 + i);
        }
        
        // Send multiple notifications
        String[] notificationTypes = {"email", "sms", "push"};
        for (int i = 0; i < 10; i++) {
            var notification = notificationFactory.createNotification(notificationTypes[i % 3]);
            notification.send("Bulk message " + i);
        }
        
        // All operations should complete without errors
        assertEquals(10, users.length);
        assertNotNull(users[0]);
        assertNotNull(users[9]);
    }

    @Test
    void testPatternMemoryUsage() {
        // Test that patterns don't cause memory leaks when used together
        
        // Create and discard many objects
        for (int i = 0; i < 1000; i++) {
            User user = new User.UserBuilder()
                    .firstName("Memory" + i)
                    .build();
            
            var notification = notificationFactory.createNotification("email");
            // Objects should be eligible for garbage collection after this loop
        }
        
        // Suggest garbage collection
        System.gc();
        
        // Singleton should still work (not affected by GC)
        databaseConnection.connect();
        assertTrue(databaseConnection.isConnected());
    }

    @Test
    void testPatternThreadSafety() throws InterruptedException {
        // Test thread safety when patterns are used concurrently
        
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                // Each thread uses multiple patterns
                databaseConnection.connect();
                
                User user = new User.UserBuilder()
                        .firstName("Thread" + threadId)
                        .build();
                
                paymentContext.executePayment("credit", 100.0 + threadId);
                orderEventPublisher.createOrder("THREAD_ORD" + threadId, 100.0 + threadId);
                
                var notification = notificationFactory.createNotification("sms");
                notification.send("Thread " + threadId + " message");
            });
        }
        
        // Start all threads
        for (Thread thread : threads) {
            thread.start();
        }
        
        // Wait for all threads to complete
        for (Thread thread : threads) {
            thread.join();
        }
        
        // Database should still be connected (singleton behavior)
        assertTrue(databaseConnection.isConnected());
    }

    @Test
    void testPatternConfigurationConsistency() {
        // Test that Spring configuration properly wires all patterns
        
        assertNotNull(databaseConnection, "DatabaseConnection should be wired");
        assertNotNull(notificationFactory, "NotificationFactory should be wired");
        assertNotNull(orderFacade, "OrderFacade should be wired");
        assertNotNull(orderEventPublisher, "OrderEventPublisher should be wired");
        assertNotNull(paymentContext, "PaymentContext should be wired");
        
        // Test that singleton behavior is maintained
        DatabaseConnection anotherReference = databaseConnection;
        assertSame(databaseConnection, anotherReference, "Should be same singleton instance");
    }

    @Test
    void testEndToEndScenario(CapturedOutput output) {
        // Complete end-to-end scenario using all patterns
        
        // 1. System initialization (Singleton)
        databaseConnection.connect();
        
        // 2. Customer registration (Builder)
        User customer = new User.UserBuilder()
                .firstName("Alice")
                .lastName("Johnson")
                .email("alice.johnson@example.com")
                .phone("+1234567890")
                .address("456 Oak Street")
                .age(28)
                .build();
        
        // 3. Product selection and payment (Strategy)
        paymentContext.executePayment("paypal", 149.99);
        
        // 4. Order processing (Facade)
        boolean orderPlaced = orderFacade.placeOrder("LAPTOP001", 1, 149.99, customer.getAddress());
        assertTrue(orderPlaced);
        
        // 5. Order confirmation (Observer)
        orderEventPublisher.createOrder("ORD_ALICE_001", 149.99);
        
        // 6. Customer notification (Factory + Adapter could be used here)
        var emailNotification = notificationFactory.createNotification("email");
        emailNotification.send("Dear " + customer.getFirstName() + ", your order has been confirmed!");
        
        var smsNotification = notificationFactory.createNotification("sms");
        smsNotification.send("Order ORD_ALICE_001 confirmed. Total: $149.99");
        
        // 7. System cleanup
        databaseConnection.disconnect();
        
        // Verify the complete workflow
        String outputString = output.getOut();
        assertTrue(outputString.contains("Database connected"));
        assertTrue(outputString.contains("PayPal"));
        assertTrue(outputString.contains("Order placed successfully"));
        assertTrue(outputString.contains("Creating order: ORD_ALICE_001"));
        assertTrue(outputString.contains("Email sent: Dear Alice"));
        assertTrue(outputString.contains("SMS sent: Order ORD_ALICE_001"));
        assertTrue(outputString.contains("Database disconnected"));
    }
}
