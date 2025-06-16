package com.patterns.behavioral;

import com.patterns.behavioral.strategy.PaymentContext;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.system.CapturedOutput;
import org.springframework.boot.test.system.OutputCaptureExtension;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@ExtendWith(OutputCaptureExtension.class)
class StrategyPatternTest {

    @Autowired
    private PaymentContext paymentContext;

    @Test
    void testPaymentContextNotNull() {
        assertNotNull(paymentContext, "PaymentContext should be injected");
    }

    @Test
    void testCreditCardPayment(CapturedOutput output) {
        paymentContext.executePayment("credit", 100.0);
        
        assertTrue(output.getOut().contains("Paid $100.0 using Credit Card"));
    }

    @Test
    void testPayPalPayment(CapturedOutput output) {
        paymentContext.executePayment("paypal", 75.50);
        
        assertTrue(output.getOut().contains("Paid $75.5 using PayPal"));
    }

    @Test
    void testBankTransferPayment(CapturedOutput output) {
        paymentContext.executePayment("bank", 250.75);
        
        assertTrue(output.getOut().contains("Paid $250.75 using Bank Transfer"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"CREDIT", "Credit", "CrEdIt"})
    void testCaseInsensitiveCreditPayment(String paymentType, CapturedOutput output) {
        paymentContext.executePayment(paymentType, 50.0);
        
        assertTrue(output.getOut().contains("Paid $50.0 using Credit Card"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"PAYPAL", "PayPal", "PaYpAl"})
    void testCaseInsensitivePayPalPayment(String paymentType, CapturedOutput output) {
        paymentContext.executePayment(paymentType, 30.0);
        
        assertTrue(output.getOut().contains("Paid $30.0 using PayPal"));
    }

    @ParameterizedTest
    @ValueSource(strings = {"BANK", "Bank", "BaNk"})
    void testCaseInsensitiveBankPayment(String paymentType, CapturedOutput output) {
        paymentContext.executePayment(paymentType, 80.0);
        
        assertTrue(output.getOut().contains("Paid $80.0 using Bank Transfer"));
    }

    @Test
    void testUnsupportedPaymentMethod(CapturedOutput output) {
        paymentContext.executePayment("bitcoin", 100.0);
        
        assertTrue(output.getOut().contains("Payment method not supported: bitcoin"));
    }

    @Test
    void testNullPaymentMethod(CapturedOutput output) {
        paymentContext.executePayment(null, 100.0);
        
        assertTrue(output.getOut().contains("Payment method not supported: null"));
    }

    @Test
    void testEmptyPaymentMethod(CapturedOutput output) {
        paymentContext.executePayment("", 100.0);
        
        assertTrue(output.getOut().contains("Payment method not supported: "));
    }

    @Test
    void testZeroAmount(CapturedOutput output) {
        paymentContext.executePayment("credit", 0.0);
        
        assertTrue(output.getOut().contains("Paid $0.0 using Credit Card"));
    }

    @Test
    void testNegativeAmount(CapturedOutput output) {
        paymentContext.executePayment("paypal", -50.0);
        
        assertTrue(output.getOut().contains("Paid $-50.0 using PayPal"));
    }

    @Test
    void testLargeAmount(CapturedOutput output) {
        paymentContext.executePayment("bank", 999999.99);
        
        assertTrue(output.getOut().contains("Paid $999999.99 using Bank Transfer"));
    }

    @Test
    void testDecimalAmount(CapturedOutput output) {
        paymentContext.executePayment("credit", 123.456);
        
        assertTrue(output.getOut().contains("Paid $123.456 using Credit Card"));
    }

    @Test
    void testMultiplePayments(CapturedOutput output) {
        paymentContext.executePayment("credit", 100.0);
        paymentContext.executePayment("paypal", 200.0);
        paymentContext.executePayment("bank", 300.0);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Paid $100.0 using Credit Card"));
        assertTrue(outputString.contains("Paid $200.0 using PayPal"));
        assertTrue(outputString.contains("Paid $300.0 using Bank Transfer"));
    }

    @Test
    void testMixedValidAndInvalidPayments(CapturedOutput output) {
        paymentContext.executePayment("credit", 100.0);
        paymentContext.executePayment("invalid", 50.0);
        paymentContext.executePayment("paypal", 75.0);
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Paid $100.0 using Credit Card"));
        assertTrue(outputString.contains("Payment method not supported: invalid"));
        assertTrue(outputString.contains("Paid $75.0 using PayPal"));
    }

    @Test
    void testPaymentMethodWithSpaces(CapturedOutput output) {
        paymentContext.executePayment(" credit ", 100.0);
        
        // Should not match due to spaces
        assertTrue(output.getOut().contains("Payment method not supported:  credit "));
    }

    @Test
    void testAllSupportedPaymentMethods(CapturedOutput output) {
        String[] supportedMethods = {"credit", "paypal", "bank"};
        double[] amounts = {100.0, 200.0, 300.0};
        
        for (int i = 0; i < supportedMethods.length; i++) {
            paymentContext.executePayment(supportedMethods[i], amounts[i]);
        }
        
        String outputString = output.getOut();
        assertTrue(outputString.contains("Credit Card"));
        assertTrue(outputString.contains("PayPal"));
        assertTrue(outputString.contains("Bank Transfer"));
    }

    @Test
    void testPaymentSequence(CapturedOutput output) {
        paymentContext.executePayment("credit", 50.0);
        paymentContext.executePayment("credit", 25.0);
        
        String outputString = output.getOut();
        String[] lines = outputString.split("\n");
        
        boolean foundFirst = false;
        boolean foundSecond = false;
        
        for (String line : lines) {
            if (line.contains("Paid $50.0 using Credit Card") && !foundFirst) {
                foundFirst = true;
            } else if (line.contains("Paid $25.0 using Credit Card") && foundFirst) {
                foundSecond = true;
                break;
            }
        }
        
        assertTrue(foundFirst && foundSecond, "Both payments should be processed in sequence");
    }

    @Test
    void testStrategyPatternFlexibility(CapturedOutput output) {
        // Test that we can switch between strategies dynamically
        paymentContext.executePayment("credit", 100.0);
        paymentContext.executePayment("paypal", 100.0);
        paymentContext.executePayment("bank", 100.0);
        paymentContext.executePayment("credit", 100.0); // Back to credit
        
        String outputString = output.getOut();
        long creditCount = outputString.lines()
                .filter(line -> line.contains("Credit Card"))
                .count();
        long paypalCount = outputString.lines()
                .filter(line -> line.contains("PayPal"))
                .count();
        long bankCount = outputString.lines()
                .filter(line -> line.contains("Bank Transfer"))
                .count();
        
        assertEquals(2, creditCount, "Should have 2 credit card payments");
        assertEquals(1, paypalCount, "Should have 1 PayPal payment");
        assertEquals(1, bankCount, "Should have 1 bank transfer payment");
    }
}
