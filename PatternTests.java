package com.patterns;

import com.patterns.creational.singleton.DatabaseConnection;
import com.patterns.creational.factory.NotificationFactory;
import com.patterns.creational.builder.User;
import com.patterns.structural.decorator.CoffeeService;
import com.patterns.behavioral.strategy.PaymentContext;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class PatternTests {

    @Autowired
    private DatabaseConnection databaseConnection;
    
    @Autowired
    private NotificationFactory notificationFactory;
    
    @Autowired
    private CoffeeService coffeeService;
    
    @Autowired
    private PaymentContext paymentContext;

    @Test
    void testSingletonPattern() {
        assertNotNull(databaseConnection);
        databaseConnection.connect();
        assertTrue(databaseConnection.isConnected());
    }
    
    @Test
    void testFactoryPattern() {
        var emailNotification = notificationFactory.createNotification("email");
        assertNotNull(emailNotification);
        
        var smsNotification = notificationFactory.createNotification("sms");
        assertNotNull(smsNotification);
    }
    
    @Test
    void testBuilderPattern() {
        User user = new User.UserBuilder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .age(25)
                .build();
        
        assertEquals("Test", user.getFirstName());
        assertEquals("User", user.getLastName());
        assertEquals("test@example.com", user.getEmail());
        assertEquals(25, user.getAge());
    }
    
    @Test
    void testDecoratorPattern() {
        var coffee = coffeeService.createCoffeeWithMilkAndSugar();
        assertNotNull(coffee);
        assertTrue(coffee.getDescription().contains("milk"));
        assertTrue(coffee.getDescription().contains("sugar"));
        assertEquals(2.7, coffee.getCost(), 0.01);
    }
    
    @Test
    void testStrategyPattern() {
        // This test mainly ensures no exceptions are thrown
        assertDoesNotThrow(() -> {
            paymentContext.executePayment("credit", 100.0);
            paymentContext.executePayment("paypal", 50.0);
            paymentContext.executePayment("bank", 200.0);
        });
    }
}
