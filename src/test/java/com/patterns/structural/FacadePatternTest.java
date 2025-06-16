package com.patterns.structural;

import com.patterns.structural.facade.OrderFacade;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class FacadePatternTest {

    @Autowired
    private OrderFacade orderFacade;

    @Test
    void testOrderFacadeNotNull() {
        assertNotNull(orderFacade, "OrderFacade should be injected");
    }

    @Test
    void testSuccessfulOrderPlacement(CapturedOutput output) {
        boolean result = orderFacade.placeOrder("PROD123", 2, 99.99, "123 Main St");
        
        assertTrue(result, "Order should be placed successfully");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Starting order process"));
        assertTrue(outputString.contains("Checking stock for product: PROD123"));
        assertTrue(outputString.contains("Reserved 2 units of PROD123"));
        assertTrue(outputString.contains("Processing payment of $99.99"));
        assertTrue(outputString.contains("Shipping scheduled to: 123 Main St"));
        assertTrue(outputString.contains("Order placed successfully!"));
    }

    @Test
    void testOrderWithDifferentProducts(CapturedOutput output) {
        boolean result1 = orderFacade.placeOrder("LAPTOP001", 1, 1299.99, "456 Oak Ave");
        boolean result2 = orderFacade.placeOrder("MOUSE002", 3, 29.99, "789 Pine St");
        
        assertTrue(result1, "First order should be successful");
        assertTrue(result2, "Second order should be successful");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("LAPTOP001"));
        assertTrue(outputString.contains("MOUSE002"));
        assertTrue(outputString.contains("456 Oak Ave"));
        assertTrue(outputString.contains("789 Pine St"));
    }

    @Test
    void testOrderWithZeroQuantity(CapturedOutput output) {
        boolean result = orderFacade.placeOrder("PROD123", 0, 0.0, "123 Main St");
        
        assertTrue(result, "Order with zero quantity should still process");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Reserved 0 units of PROD123"));
        assertTrue(outputString.contains("Processing payment of $0.0"));
    }

    @Test
    void testOrderWithNegativeQuantity(CapturedOutput output) {
        boolean result = orderFacade.placeOrder("PROD123", -1, 50.0, "123 Main St");
        
        assertTrue(result, "Order should process even with negative quantity");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Reserved -1 units of PROD123"));
    }

    @Test
    void testOrderWithNegativeAmount(CapturedOutput output) {
        boolean result = orderFacade.placeOrder("PROD123", 1, -50.0, "123 Main St");
        
        assertTrue(result, "Order should process even with negative amount");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Processing payment of $-50.0"));
    }

    @Test
    void testOrderWithNullProductId(CapturedOutput output) {
        boolean result = orderFacade.placeOrder(null, 1, 50.0, "123 Main St");
        
        assertTrue(result, "Order should process with null product ID");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Checking stock for product: null"));
        assertTrue(outputString.contains("Reserved 1 units of null"));
    }

    @Test
    void testOrderWithEmptyProductId(CapturedOutput output) {
        boolean result = orderFacade.placeOrder("", 1, 50.0, "123 Main St");
        
        assertTrue(result, "Order should process with empty product ID");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Checking stock for product: "));
        assertTrue(outputString.contains("Reserved 1 units of "));
    }

    @Test
    void testOrderWithNullAddress(CapturedOutput output) {
        boolean result = orderFacade.placeOrder("PROD123", 1, 50.0, null);
        
        assertTrue(result, "Order should process with null address");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Shipping scheduled to: null"));
    }

    @Test
    void testOrderWithEmptyAddress(CapturedOutput output) {
        boolean result = orderFacade.placeOrder("PROD123", 1, 50.0, "");
        
        assertTrue(result, "Order should process with empty address");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Shipping scheduled to: "));
    }

    @Test
    void testMultipleOrdersSequentially(CapturedOutput output) {
        boolean result1 = orderFacade.placeOrder("PROD1", 1, 10.0, "Address 1");
        boolean result2 = orderFacade.placeOrder("PROD2", 2, 20.0, "Address 2");
        boolean result3 = orderFacade.placeOrder("PROD3", 3, 30.0, "Address 3");
        
        assertTrue(result1, "First order should be successful");
        assertTrue(result2, "Second order should be successful");
        assertTrue(result3, "Third order should be successful");
        
        String outputString = output.getOut();
        // Check that all orders were processed
        long orderCount = outputString.lines()
                .filter(line -> line.contains("Order placed successfully!"))
                .count();
        assertEquals(3, orderCount, "Should have 3 successful orders");
    }

    @Test
    void testOrderProcessingSteps(CapturedOutput output) {
        orderFacade.placeOrder("TEST123", 5, 250.0, "Test Address");
        
        String outputString = output.getOut();
        String[] lines = outputString.split("\n");
        
        // Verify the order of operations
        boolean foundStart = false, foundStock = false, foundReserve = false, 
               foundPayment = false, foundShipping = false, foundSuccess = false;
        
        for (String line : lines) {
            if (line.contains("Starting order process")) foundStart = true;
            else if (line.contains("Checking stock") && foundStart) foundStock = true;
            else if (line.contains("Reserved") && foundStock) foundReserve = true;
            else if (line.contains("Processing payment") && foundReserve) foundPayment = true;
            else if (line.contains("Shipping scheduled") && foundPayment) foundShipping = true;
            else if (line.contains("Order placed successfully") && foundShipping) foundSuccess = true;
        }
        
        assertTrue(foundSuccess, "All steps should be executed in correct order");
    }

    @Test
    void testLargeOrderValues(CapturedOutput output) {
        boolean result = orderFacade.placeOrder("EXPENSIVE_ITEM", 1000, 999999.99, 
                                               "Very Long Address Name That Might Cause Issues");
        
        assertTrue(result, "Large order should be processed successfully");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Reserved 1000 units"));
        assertTrue(outputString.contains("Processing payment of $999999.99"));
    }
}
