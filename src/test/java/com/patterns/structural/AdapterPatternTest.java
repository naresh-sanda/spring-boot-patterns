package com.patterns.structural;

import com.patterns.structural.adapter.PaymentAdapter;
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
class AdapterPatternTest {

    @Autowired
    private PaymentAdapter paymentAdapter;

    @Test
    void testPaymentAdapterNotNull() {
        assertNotNull(paymentAdapter, "PaymentAdapter should be injected");
    }

    @Test
    void testProcessPaymentWithValidAmount(CapturedOutput output) {
        assertDoesNotThrow(() -> {
            paymentAdapter.processPayment("100.50", "USD");
        }, "Processing valid payment should not throw exception");
        
        assertTrue(output.getOut().contains("Adapting payment for currency: USD"));
        assertTrue(output.getOut().contains("Legacy payment of $100.5 processed"));
    }

    @Test
    void testProcessPaymentWithDifferentCurrencies(CapturedOutput output) {
        paymentAdapter.processPayment("50.25", "EUR");
        assertTrue(output.getOut().contains("Adapting payment for currency: EUR"));
        
        paymentAdapter.processPayment("75.75", "GBP");
        assertTrue(output.getOut().contains("Adapting payment for currency: GBP"));
    }

    @Test
    void testProcessPaymentWithZeroAmount(CapturedOutput output) {
        assertDoesNotThrow(() -> {
            paymentAdapter.processPayment("0.00", "USD");
        }, "Processing zero amount should not throw exception");
        
        assertTrue(output.getOut().contains("Legacy payment of $0.0 processed"));
    }

    @Test
    void testProcessPaymentWithLargeAmount(CapturedOutput output) {
        assertDoesNotThrow(() -> {
            paymentAdapter.processPayment("999999.99", "USD");
        }, "Processing large amount should not throw exception");
        
        assertTrue(output.getOut().contains("Legacy payment of $999999.99 processed"));
    }

    @Test
    void testProcessPaymentWithInvalidAmount() {
        assertThrows(NumberFormatException.class, () -> {
            paymentAdapter.processPayment("invalid", "USD");
        }, "Invalid amount should throw NumberFormatException");
    }

    @Test
    void testProcessPaymentWithNullAmount() {
        assertThrows(NullPointerException.class, () -> {
            paymentAdapter.processPayment(null, "USD");
        }, "Null amount should throw NullPointerException");
    }

    @Test
    void testProcessPaymentWithEmptyAmount() {
        assertThrows(NumberFormatException.class, () -> {
            paymentAdapter.processPayment("", "USD");
        }, "Empty amount should throw NumberFormatException");
    }

    @Test
    void testProcessPaymentWithNullCurrency(CapturedOutput output) {
        assertDoesNotThrow(() -> {
            paymentAdapter.processPayment("100.00", null);
        }, "Null currency should not throw exception");
        
        assertTrue(output.getOut().contains("Adapting payment for currency: null"));
    }

    @Test
    void testProcessPaymentWithEmptyCurrency(CapturedOutput output) {
        assertDoesNotThrow(() -> {
            paymentAdapter.processPayment("100.00", "");
        }, "Empty currency should not throw exception");
        
        assertTrue(output.getOut().contains("Adapting payment for currency: "));
    }

    @Test
    void testProcessPaymentWithNegativeAmount(CapturedOutput output) {
        assertDoesNotThrow(() -> {
            paymentAdapter.processPayment("-50.00", "USD");
        }, "Negative amount should not throw exception");
        
        assertTrue(output.getOut().contains("Legacy payment of $-50.0 processed"));
    }

    @Test
    void testProcessPaymentWithDecimalAmount(CapturedOutput output) {
        paymentAdapter.processPayment("123.456", "USD");
        assertTrue(output.getOut().contains("Legacy payment of $123.456 processed"));
    }

    @Test
    void testMultiplePaymentProcessing(CapturedOutput output) {
        paymentAdapter.processPayment("100.00", "USD");
        paymentAdapter.processPayment("200.00", "EUR");
        paymentAdapter.processPayment("300.00", "GBP");
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Legacy payment of $100.0 processed"));
        assertTrue(outputString.contains("Legacy payment of $200.0 processed"));
        assertTrue(outputString.contains("Legacy payment of $300.0 processed"));
    }
}
