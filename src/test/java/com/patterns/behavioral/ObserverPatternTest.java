package com.patterns.behavioral;

import com.patterns.behavioral.observer.OrderEventPublisher;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class ObserverPatternTest {

    @Autowired
    private OrderEventPublisher orderEventPublisher;

    @Test
    void testOrderEventPublisherNotNull() {
        assertNotNull(orderEventPublisher, "OrderEventPublisher should be injected");
    }

    @Test
    void testOrderCreationTriggersObservers(CapturedOutput output) {
        orderEventPublisher.createOrder("ORD123", 150.0);
        
        String outputString = output.getOut();
        
        // Check that order creation is logged
        assertTrue(outputString.contains("Creating order: ORD123"));
        
        // Check that email notification listener is triggered
        assertTrue(outputString.contains("Email notification: Order ORD123 created with amount $150.0"));
        
        // Check that inventory update listener is triggered
        assertTrue(outputString.contains("Inventory update: Processing order ORD123"));
    }

    @Test
    void testMultipleOrderCreations(CapturedOutput output) {
        orderEventPublisher.createOrder("ORD001", 100.0);
        orderEventPublisher.createOrder("ORD002", 200.0);
        orderEventPublisher.createOrder("ORD003", 300.0);
        
        String outputString = output.getOut();
        
        // Check all orders are created
        assertTrue(outputString.contains("Creating order: ORD001"));
        assertTrue(outputString.contains("Creating order: ORD002"));
        assertTrue(outputString.contains("Creating order: ORD003"));
        
        // Check all email notifications
        assertTrue(outputString.contains("Email notification: Order ORD001 created with amount $100.0"));
        assertTrue(outputString.contains("Email notification: Order ORD002 created with amount $200.0"));
        assertTrue(outputString.contains("Email notification: Order ORD003 created with amount $300.0"));
        
        // Check all inventory updates
        assertTrue(outputString.contains("Inventory update: Processing order ORD001"));
        assertTrue(outputString.contains("Inventory update: Processing order ORD002"));
        assertTrue(outputString.contains("Inventory update: Processing order ORD003"));
    }

    @Test
    void testOrderWithZeroAmount(CapturedOutput output) {
        orderEventPublisher.createOrder("ORD000", 0.0);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Creating order: ORD000"));
        assertTrue(outputString.contains("Email notification: Order ORD000 created with amount $0.0"));
        assertTrue(outputString.contains("Inventory update: Processing order ORD000"));
    }

    @Test
    void testOrderWithNegativeAmount(CapturedOutput output) {
        orderEventPublisher.createOrder("ORD_NEG", -50.0);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Creating order: ORD_NEG"));
        assertTrue(outputString.contains("Email notification: Order ORD_NEG created with amount $-50.0"));
        assertTrue(outputString.contains("Inventory update: Processing order ORD_NEG"));
    }

    @Test
    void testOrderWithNullOrderId(CapturedOutput output) {
        orderEventPublisher.createOrder(null, 100.0);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Creating order: null"));
        assertTrue(outputString.contains("Email notification: Order null created with amount $100.0"));
        assertTrue(outputString.contains("Inventory update: Processing order null"));
    }

    @Test
    void testOrderWithEmptyOrderId(CapturedOutput output) {
        orderEventPublisher.createOrder("", 100.0);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Creating order: "));
        assertTrue(outputString.contains("Email notification: Order  created with amount $100.0"));
        assertTrue(outputString.contains("Inventory update: Processing order "));
    }

    @Test
    void testOrderWithLargeAmount(CapturedOutput output) {
        orderEventPublisher.createOrder("ORD_LARGE", 999999.99);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Creating order: ORD_LARGE"));
        assertTrue(outputString.contains("Email notification: Order ORD_LARGE created with amount $999999.99"));
        assertTrue(outputString.contains("Inventory update: Processing order ORD_LARGE"));
    }

    @Test
    void testOrderWithSpecialCharacters(CapturedOutput output) {
        orderEventPublisher.createOrder("ORD@#$%", 123.45);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Creating order: ORD@#$%"));
        assertTrue(outputString.contains("Email notification: Order ORD@#$% created with amount $123.45"));
        assertTrue(outputString.contains("Inventory update: Processing order ORD@#$%"));
    }

    @Test
    void testObserverEventSequence(CapturedOutput output) {
        orderEventPublisher.createOrder("SEQ_TEST", 75.0);
        
        String outputString = output.getOut();
        String[] lines = outputString.split("\n");
        
        boolean foundCreation = false;
        boolean foundEmailAfterCreation = false;
        boolean foundInventoryAfterCreation = false;
        
        for (String line : lines) {
            if (line.contains("Creating order: SEQ_TEST")) {
                foundCreation = true;
            } else if (foundCreation && line.contains("Email notification: Order SEQ_TEST")) {
                foundEmailAfterCreation = true;
            } else if (foundCreation && line.contains("Inventory update: Processing order SEQ_TEST")) {
                foundInventoryAfterCreation = true;
            }
        }
        
        assertTrue(foundCreation, "Order creation should be logged");
        assertTrue(foundEmailAfterCreation, "Email notification should occur after creation");
        assertTrue(foundInventoryAfterCreation, "Inventory update should occur after creation");
    }

    @Test
    void testConcurrentOrderCreation(CapturedOutput output) throws InterruptedException {
        // Test multiple orders created in quick succession
        Thread thread1 = new Thread(() -> orderEventPublisher.createOrder("CONCURRENT1", 100.0));
        Thread thread2 = new Thread(() -> orderEventPublisher.createOrder("CONCURRENT2", 200.0));
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("CONCURRENT1"));
        assertTrue(outputString.contains("CONCURRENT2"));
    }

    @Test
    void testEventListenerCount(CapturedOutput output) {
        orderEventPublisher.createOrder("LISTENER_COUNT", 50.0);
        
        String outputString = output.getOut();
        
        // Count how many listeners responded (should be 2: email and inventory)
        long emailNotifications = outputString.lines()
                .filter(line -> line.contains("Email notification"))
                .count();
        long inventoryUpdates = outputString.lines()
                .filter(line -> line.contains("Inventory update"))
                .count();
        
        assertEquals(1, emailNotifications, "Should have exactly 1 email notification");
        assertEquals(1, inventoryUpdates, "Should have exactly 1 inventory update");
    }
}
