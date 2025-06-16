package com.patterns.creational;

import com.patterns.creational.factory.NotificationFactory;
import com.patterns.creational.factory.Notification;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class FactoryPatternTest {

    @Autowired
    private NotificationFactory notificationFactory;

    @Test
    void testEmailNotificationCreation() {
        Notification notification = notificationFactory.createNotification("email");
        
        assertNotNull(notification, "Email notification should not be null");
        assertDoesNotThrow(() -> notification.send("Test email message"), 
                          "Email notification should send without throwing exception");
    }

    @Test
    void testSMSNotificationCreation() {
        Notification notification = notificationFactory.createNotification("sms");
        
        assertNotNull(notification, "SMS notification should not be null");
        assertDoesNotThrow(() -> notification.send("Test SMS message"), 
                          "SMS notification should send without throwing exception");
    }

    @Test
    void testPushNotificationCreation() {
        Notification notification = notificationFactory.createNotification("push");
        
        assertNotNull(notification, "Push notification should not be null");
        assertDoesNotThrow(() -> notification.send("Test push message"), 
                          "Push notification should send without throwing exception");
    }

    @ParameterizedTest
    @ValueSource(strings = {"EMAIL", "SMS", "PUSH", "Email", "Sms", "Push"})
    void testCaseInsensitiveNotificationCreation(String type) {
        assertDoesNotThrow(() -> {
            Notification notification = notificationFactory.createNotification(type);
            assertNotNull(notification, "Notification should be created for type: " + type);
        }, "Factory should handle case-insensitive input: " + type);
    }

    @Test
    void testInvalidNotificationType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationFactory.createNotification("invalid"),
            "Factory should throw exception for invalid type"
        );
        
        assertTrue(exception.getMessage().contains("Unknown notification type"), 
                  "Exception message should indicate unknown type");
    }

    @Test
    void testNullNotificationType() {
        assertThrows(
            NullPointerException.class,
            () -> notificationFactory.createNotification(null),
            "Factory should throw exception for null type"
        );
    }

    @Test
    void testEmptyNotificationType() {
        IllegalArgumentException exception = assertThrows(
            IllegalArgumentException.class,
            () -> notificationFactory.createNotification(""),
            "Factory should throw exception for empty type"
        );
        
        assertTrue(exception.getMessage().contains("Unknown notification type"), 
                  "Exception message should indicate unknown type");
    }

    @Test
    void testMultipleInstancesAreDifferent() {
        Notification email1 = notificationFactory.createNotification("email");
        Notification email2 = notificationFactory.createNotification("email");
        
        // Factory creates new instances each time (not singleton)
        assertNotSame(email1, email2, 
                     "Factory should create different instances each time");
    }

    @Test
    void testDifferentTypesAreDifferentClasses() {
        Notification email = notificationFactory.createNotification("email");
        Notification sms = notificationFactory.createNotification("sms");
        Notification push = notificationFactory.createNotification("push");
        
        assertNotEquals(email.getClass(), sms.getClass(), 
                       "Email and SMS should be different classes");
        assertNotEquals(email.getClass(), push.getClass(), 
                       "Email and Push should be different classes");
        assertNotEquals(sms.getClass(), push.getClass(), 
                       "SMS and Push should be different classes");
    }
}
