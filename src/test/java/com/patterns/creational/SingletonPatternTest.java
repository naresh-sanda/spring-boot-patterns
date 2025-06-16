package com.patterns.creational;

import com.patterns.creational.singleton.DatabaseConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class SingletonPatternTest {

    @Autowired
    private ApplicationContext applicationContext;
    
    @Autowired
    private DatabaseConnection databaseConnection1;
    
    @Autowired
    private DatabaseConnection databaseConnection2;

    @BeforeEach
    void setUp() {
        // Reset connection state before each test
        if (databaseConnection1.isConnected()) {
            databaseConnection1.disconnect();
        }
    }

    @Test
    void testSingletonInstance() {
        // Test that Spring creates only one instance
        assertSame(databaseConnection1, databaseConnection2, 
                  "Both injected instances should be the same object");
    }

    @Test
    void testSingletonFromApplicationContext() {
        // Test getting instances from application context
        DatabaseConnection instance1 = applicationContext.getBean(DatabaseConnection.class);
        DatabaseConnection instance2 = applicationContext.getBean(DatabaseConnection.class);
        
        assertSame(instance1, instance2, 
                  "Instances from application context should be the same");
        assertSame(databaseConnection1, instance1, 
                  "Injected and context instances should be the same");
    }

    @Test
    void testConnectionFunctionality() {
        // Test initial state
        assertFalse(databaseConnection1.isConnected(), 
                   "Connection should be initially disconnected");
        
        // Test connection
        databaseConnection1.connect();
        assertTrue(databaseConnection1.isConnected(), 
                  "Connection should be established");
        
        // Test that the same instance reflects the state change
        assertTrue(databaseConnection2.isConnected(), 
                  "Second reference should show same connection state");
        
        // Test disconnection
        databaseConnection1.disconnect();
        assertFalse(databaseConnection1.isConnected(), 
                   "Connection should be disconnected");
        assertFalse(databaseConnection2.isConnected(), 
                   "Second reference should show same disconnection state");
    }

    @Test
    void testMultipleConnectCalls() {
        // Test multiple connect calls don't cause issues
        databaseConnection1.connect();
        assertTrue(databaseConnection1.isConnected());
        
        databaseConnection1.connect(); // Second call
        assertTrue(databaseConnection1.isConnected(), 
                  "Multiple connect calls should not affect state");
    }

    @Test
    void testMultipleDisconnectCalls() {
        // Test multiple disconnect calls don't cause issues
        databaseConnection1.connect();
        databaseConnection1.disconnect();
        assertFalse(databaseConnection1.isConnected());
        
        databaseConnection1.disconnect(); // Second call
        assertFalse(databaseConnection1.isConnected(), 
                   "Multiple disconnect calls should not affect state");
    }
}
