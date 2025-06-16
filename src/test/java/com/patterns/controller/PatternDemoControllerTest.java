package com.patterns.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureTestMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.util.HashMap;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureTestMvc
class PatternDemoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testSingletonEndpoint() throws Exception {
        mockMvc.perform(get("/api/patterns/singleton"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.connected").value(true))
                .andExpect(jsonPath("$.pattern").value("Singleton Pattern - Single instance managed by Spring"));
    }

    @Test
    void testFactoryEndpointWithEmail() throws Exception {
        mockMvc.perform(get("/api/patterns/factory/email"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("email"))
                .andExpect(jsonPath("$.pattern").value("Factory Pattern - Creates objects without specifying exact classes"));
    }

    @Test
    void testFactoryEndpointWithSMS() throws Exception {
        mockMvc.perform(get("/api/patterns/factory/sms"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("sms"));
    }

    @Test
    void testFactoryEndpointWithPush() throws Exception {
        mockMvc.perform(get("/api/patterns/factory/push"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.type").value("push"));
    }

    @Test
    void testFactoryEndpointWithInvalidType() throws Exception {
        mockMvc.perform(get("/api/patterns/factory/invalid"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(false))
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    void testBuilderEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/patterns/builder"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").exists())
                .andExpect(jsonPath("$.pattern").value("Builder Pattern - Constructs complex objects step by step"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("John"));
        assertTrue(content.contains("Doe"));
        assertTrue(content.contains("john.doe@example.com"));
    }

    @Test
    void testAdapterEndpoint() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("amount", "100.50");
        request.put("currency", "USD");

        mockMvc.perform(post("/api/patterns/adapter/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pattern").value("Adapter Pattern - Allows incompatible interfaces to work together"));
    }

    @Test
    void testDecoratorEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/patterns/decorator/coffee"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.description").exists())
                .andExpected(jsonPath("$.cost").exists())
                .andExpect(jsonPath("$.pattern").value("Decorator Pattern - Adds behavior to objects dynamically"))
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("milk"));
        assertTrue(content.contains("sugar"));
    }

    @Test
    void testFacadeEndpoint() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("productId", "TEST123");
        request.put("quantity", 2);
        request.put("amount", 99.99);
        request.put("address", "123 Test Street");

        mockMvc.perform(post("/api/patterns/facade/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pattern").value("Facade Pattern - Provides simplified interface to complex subsystems"));
    }

    @Test
    void testObserverEndpoint() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("orderId", "OBS123");
        request.put("amount", 150.0);

        mockMvc.perform(post("/api/patterns/observer/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pattern").value("Observer Pattern - Notifies multiple objects about state changes"));
    }

    @Test
    void testStrategyEndpoint() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("paymentType", "credit");
        request.put("amount", 75.0);

        mockMvc.perform(post("/api/patterns/strategy/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pattern").value("Strategy Pattern - Defines family of algorithms and makes them interchangeable"));
    }

    @Test
    void testCommandEndpointOn() throws Exception {
        mockMvc.perform(post("/api/patterns/command/light/on"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.action").value("on"))
                .andExpect(jsonPath("$.pattern").value("Command Pattern - Encapsulates requests as objects"));
    }

    @Test
    void testCommandEndpointUndo() throws Exception {
        mockMvc.perform(post("/api/patterns/command/light/undo"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.action").value("undo"));
    }

    @Test
    void testTemplateEndpoint() throws Exception {
        mockMvc.perform(get("/api/patterns/template/csv"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.pattern").value("Template Method Pattern - Defines skeleton of algorithm in base class"));
    }

    @Test
    void testStateEndpointNext() throws Exception {
        mockMvc.perform(post("/api/patterns/state/order/next"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.action").value("next"))
                .andExpect(jsonPath("$.pattern").value("State Pattern - Changes object behavior based on internal state"));
    }

    @Test
    void testStateEndpointPrev() throws Exception {
        mockMvc.perform(post("/api/patterns/state/order/prev"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.action").value("prev"));
    }

    @Test
    void testGetAllPatternsEndpoint() throws Exception {
        MvcResult result = mockMvc.perform(get("/api/patterns/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.creational").isArray())
                .andExpect(jsonPath("$.structural").isArray())
                .andExpect(jsonPath("$.behavioral").isArray())
                .andReturn();

        String content = result.getResponse().getContentAsString();
        assertTrue(content.contains("Singleton"));
        assertTrue(content.contains("Factory"));
        assertTrue(content.contains("Builder"));
        assertTrue(content.contains("Adapter"));
        assertTrue(content.contains("Decorator"));
        assertTrue(content.contains("Facade"));
        assertTrue(content.contains("Observer"));
        assertTrue(content.contains("Strategy"));
        assertTrue(content.contains("Command"));
        assertTrue(content.contains("Template Method"));
        assertTrue(content.contains("State"));
    }

    @Test
    void testInvalidEndpoint() throws Exception {
        mockMvc.perform(get("/api/patterns/nonexistent"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testAdapterEndpointWithInvalidData() throws Exception {
        Map<String, String> request = new HashMap<>();
        request.put("amount", "invalid");
        request.put("currency", "USD");

        mockMvc.perform(post("/api/patterns/adapter/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void testFacadeEndpointWithMissingData() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("productId", "TEST123");
        // Missing other required fields

        mockMvc.perform(post("/api/patterns/facade/order")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testStrategyEndpointWithUnsupportedPayment() throws Exception {
        Map<String, Object> request = new HashMap<>();
        request.put("paymentType", "bitcoin");
        request.put("amount", 100.0);

        mockMvc.perform(post("/api/patterns/strategy/payment")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true)); // Should still return success even if payment method is unsupported
    }

    @Test
    void testMultipleEndpointCalls() throws Exception {
        // Test calling multiple endpoints in sequence
        mockMvc.perform(get("/api/patterns/singleton"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/patterns/factory/email"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/patterns/builder"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/patterns/decorator/coffee"))
                .andExpect(status().isOk());

        mockMvc.perform(get("/api/patterns/template/csv"))
                .andExpect(status().isOk());
    }

    @Test
    void testConcurrentEndpointCalls() throws Exception {
        // Test concurrent access to endpoints
        Thread[] threads = new Thread[5];
        
        for (int i = 0; i < 5; i++) {
            final int threadId = i;
            threads[i] = new Thread(() -> {
                try {
                    mockMvc.perform(get("/api/patterns/singleton"))
                            .andExpect(status().isOk());
                    
                    mockMvc.perform(get("/api/patterns/factory/email"))
                            .andExpect(status().isOk());
                } catch (Exception e) {
                    fail("Concurrent endpoint call failed: " + e.getMessage());
                }
            });
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    @Test
    void testEndpointResponseTimes() throws Exception {
        // Test that endpoints respond within reasonable time
        long startTime = System.currentTimeMillis();
        
        mockMvc.perform(get("/api/patterns/all"))
                .andExpect(status().isOk());
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        assertTrue(duration < 1000, "Endpoint should respond within 1 second");
    }

    @Test
    void testEndpointContentTypes() throws Exception {
        // Test that all endpoints return proper content types
        mockMvc.perform(get("/api/patterns/singleton"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/patterns/builder"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));

        mockMvc.perform(get("/api/patterns/all"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON));
    }
}
