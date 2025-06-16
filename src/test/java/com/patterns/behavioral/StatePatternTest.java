package com.patterns.behavioral;

import com.patterns.behavioral.state.OrderContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class StatePatternTest {

    @Autowired
    private OrderContext orderContext;

    @BeforeEach
    void setUp() {
        // Reset to initial state before each test
        // Since we can't directly reset, we'll create a new instance for some tests
    }

    @Test
    void testOrderContextNotNull() {
        assertNotNull(orderContext, "OrderContext should be injected");
    }

    @Test
    void testInitialState(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        newContext.printStatus();
        
        assertTrue(output.getOut().contains("Order Status: PENDING"));
    }

    @Test
    void testStateTransitionFromPendingToConfirmed(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        newContext.nextState();
        newContext.printStatus();
        
        assertTrue(output.getOut().contains("Order Status: CONFIRMED"));
    }

    @Test
    void testStateTransitionFromConfirmedToShipped(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        newContext.printStatus();
        
        assertTrue(output.getOut().contains("Order Status: SHIPPED"));
    }

    @Test
    void testStateTransitionFromShippedToDelivered(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        newContext.nextState(); // SHIPPED -> DELIVERED
        newContext.printStatus();
        
        assertTrue(output.getOut().contains("Order Status: DELIVERED"));
    }

    @Test
    void testCompleteForwardStateTransition(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        newContext.printStatus(); // PENDING
        newContext.nextState();
        newContext.printStatus(); // CONFIRMED
        newContext.nextState();
        newContext.printStatus(); // SHIPPED
        newContext.nextState();
        newContext.printStatus(); // DELIVERED
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Order Status: PENDING"));
        assertTrue(outputString.contains("Order Status: CONFIRMED"));
        assertTrue(outputString.contains("Order Status: SHIPPED"));
        assertTrue(outputString.contains("Order Status: DELIVERED"));
    }

    @Test
    void testBackwardStateTransitionFromDelivered(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Go to delivered state
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        newContext.nextState(); // SHIPPED -> DELIVERED
        
        // Go back
        newContext.prevState(); // DELIVERED -> SHIPPED
        newContext.printStatus();
        
        assertTrue(output.getOut().contains("Order Status: SHIPPED"));
    }

    @Test
    void testBackwardStateTransitionFromShipped(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Go to shipped state
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        
        // Go back
        newContext.prevState(); // SHIPPED -> CONFIRMED
        newContext.printStatus();
        
        assertTrue(output.getOut().contains("Order Status: CONFIRMED"));
    }

    @Test
    void testBackwardStateTransitionFromConfirmed(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Go to confirmed state
        newContext.nextState(); // PENDING -> CONFIRMED
        
        // Go back
        newContext.prevState(); // CONFIRMED -> PENDING
        newContext.printStatus();
        
        assertTrue(output.getOut().contains("Order Status: PENDING"));
    }

    @Test
    void testPreviousFromInitialState(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        newContext.prevState(); // Should handle gracefully
        
        assertTrue(output.getOut().contains("Order is in initial state"));
    }

    @Test
    void testNextFromFinalState(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Go to final state
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        newContext.nextState(); // SHIPPED -> DELIVERED
        
        // Try to go further
        newContext.nextState(); // Should handle gracefully
        
        assertTrue(output.getOut().contains("Order is in final state"));
    }

    @Test
    void testCompleteBackwardStateTransition(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Go forward to delivered
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        newContext.nextState(); // SHIPPED -> DELIVERED
        
        // Go all the way back
        newContext.prevState(); // DELIVERED -> SHIPPED
        newContext.printStatus();
        newContext.prevState(); // SHIPPED -> CONFIRMED
        newContext.printStatus();
        newContext.prevState(); // CONFIRMED -> PENDING
        newContext.printStatus();
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Order Status: SHIPPED"));
        assertTrue(outputString.contains("Order Status: CONFIRMED"));
        assertTrue(outputString.contains("Order Status: PENDING"));
    }

    @Test
    void testAlternatingStateTransitions(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.prevState(); // CONFIRMED -> PENDING
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        newContext.prevState(); // SHIPPED -> CONFIRMED
        newContext.printStatus();
        
        assertTrue(output.getOut().contains("Order Status: CONFIRMED"));
    }

    @Test
    void testMultipleNextCallsFromFinalState(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Go to final state
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        newContext.nextState(); // SHIPPED -> DELIVERED
        
        // Multiple calls from final state
        newContext.nextState();
        newContext.nextState();
        newContext.nextState();
        
        String outputString = output.getOut();
        long finalStateMessages = outputString.lines()
                .filter(line -> line.contains("Order is in final state"))
                .count();
        
        assertEquals(3, finalStateMessages, "Should have 3 final state messages");
    }

    @Test
    void testMultiplePrevCallsFromInitialState(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Multiple calls from initial state
        newContext.prevState();
        newContext.prevState();
        newContext.prevState();
        
        String outputString = output.getOut();
        long initialStateMessages = outputString.lines()
                .filter(line -> line.contains("Order is in initial state"))
                .count();
        
        assertEquals(3, initialStateMessages, "Should have 3 initial state messages");
    }

    @Test
    void testStateTransitionSequence(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Test specific sequence
        newContext.printStatus(); // PENDING
        newContext.nextState();   // -> CONFIRMED
        newContext.printStatus(); // CONFIRMED
        newContext.nextState();   // -> SHIPPED
        newContext.printStatus(); // SHIPPED
        newContext.prevState();   // -> CONFIRMED
        newContext.printStatus(); // CONFIRMED
        newContext.nextState();   // -> SHIPPED
        newContext.printStatus(); // SHIPPED
        newContext.nextState();   // -> DELIVERED
        newContext.printStatus(); // DELIVERED
        
        String outputString = output.getOut();
        String[] lines = outputString.split("\n");
        
        // Count status messages
        long pendingCount = outputString.lines()
                .filter(line -> line.contains("Order Status: PENDING"))
                .count();
        long confirmedCount = outputString.lines()
                .filter(line -> line.contains("Order Status: CONFIRMED"))
                .count();
        long shippedCount = outputString.lines()
                .filter(line -> line.contains("Order Status: SHIPPED"))
                .count();
        long deliveredCount = outputString.lines()
                .filter(line -> line.contains("Order Status: DELIVERED"))
                .count();
        
        assertEquals(1, pendingCount, "Should have 1 pending status");
        assertEquals(2, confirmedCount, "Should have 2 confirmed statuses");
        assertEquals(2, shippedCount, "Should have 2 shipped statuses");
        assertEquals(1, deliveredCount, "Should have 1 delivered status");
    }

    @Test
    void testStatePatternEncapsulation() {
        OrderContext newContext = new OrderContext();
        
        // Test that state changes are properly encapsulated
        // We can't directly access the state, only through context methods
        
        newContext.nextState();
        newContext.nextState();
        
        // The only way to verify state is through printStatus
        // This tests that the state pattern properly encapsulates state logic
        assertDoesNotThrow(() -> newContext.printStatus(), 
                          "State pattern should encapsulate state properly");
    }

    @Test
    void testConcurrentStateTransitions() throws InterruptedException {
        OrderContext newContext = new OrderContext();
        
        Thread thread1 = new Thread(() -> {
            newContext.nextState();
            newContext.nextState();
        });
        
        Thread thread2 = new Thread(() -> {
            newContext.prevState();
            newContext.nextState();
        });
        
        thread1.start();
        thread2.start();
        
        thread1.join();
        thread2.join();
        
        // State should be in some valid state (no exceptions thrown)
        assertDoesNotThrow(() -> newContext.printStatus());
    }

    @Test
    void testStateTransitionBoundaries(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Test boundary conditions
        newContext.prevState(); // From initial state
        newContext.nextState(); // PENDING -> CONFIRMED
        newContext.nextState(); // CONFIRMED -> SHIPPED
        newContext.nextState(); // SHIPPED -> DELIVERED
        newContext.nextState(); // From final state
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Order is in initial state"));
        assertTrue(outputString.contains("Order is in final state"));
    }

    @Test
    void testStatePatternPolymorphism(CapturedOutput output) {
        OrderContext newContext = new OrderContext();
        
        // Test that different states handle the same method calls differently
        newContext.printStatus(); // PENDING state behavior
        newContext.nextState();
        newContext.printStatus(); // CONFIRMED state behavior
        newContext.nextState();
        newContext.printStatus(); // SHIPPED state behavior
        newContext.nextState();
        newContext.printStatus(); // DELIVERED state behavior
        
        String outputString = output.getOut();
        
        // Each state should produce different output for printStatus
        assertTrue(outputString.contains("PENDING"));
        assertTrue(outputString.contains("CONFIRMED"));
        assertTrue(outputString.contains("SHIPPED"));
        assertTrue(outputString.contains("DELIVERED"));
    }
}
