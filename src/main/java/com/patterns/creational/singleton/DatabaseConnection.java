package com.patterns.creational.singleton;

import org.springframework.stereotype.Component;

/**
 * Singleton Pattern - Ensures only one instance exists
 * Spring manages this as a singleton by default
 */
@Component
public class DatabaseConnection {
    private static DatabaseConnection instance;
    private boolean connected = false;
    
    public DatabaseConnection() {
        // Spring will create only one instance
        System.out.println("DatabaseConnection instance created");
    }
    
    public void connect() {
        if (!connected) {
            connected = true;
            System.out.println("Database connected");
        }
    }
    
    public void disconnect() {
        if (connected) {
            connected = false;
            System.out.println("Database disconnected");
        }
    }
    
    public boolean isConnected() {
        return connected;
    }
}
