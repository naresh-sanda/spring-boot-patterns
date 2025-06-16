package com.patterns.creational.factory;

import org.springframework.stereotype.Component;

/**
 * Factory Pattern - Creates objects without specifying exact classes
 */
public interface Notification {
    void send(String message);
}

@Component
class EmailNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Email sent: " + message);
    }
}

@Component
class SMSNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("SMS sent: " + message);
    }
}

@Component
class PushNotification implements Notification {
    @Override
    public void send(String message) {
        System.out.println("Push notification sent: " + message);
    }
}

@Component
public class NotificationFactory {
    
    public Notification createNotification(String type) {
        switch (type.toLowerCase()) {
            case "email":
                return new EmailNotification();
            case "sms":
                return new SMSNotification();
            case "push":
                return new PushNotification();
            default:
                throw new IllegalArgumentException("Unknown notification type: " + type);
        }
    }
}
