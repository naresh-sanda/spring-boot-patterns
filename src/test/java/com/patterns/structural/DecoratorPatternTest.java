package com.patterns.structural;

import com.patterns.structural.decorator.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DecoratorPatternTest {

    @Autowired
    private CoffeeService coffeeService;

    @Test
    void testCoffeeServiceNotNull() {
        assertNotNull(coffeeService, "CoffeeService should be injected");
    }

    @Test
    void testSimpleCoffee() {
        Coffee simpleCoffee = new SimpleCoffee();
        
        assertEquals("Simple coffee", simpleCoffee.getDescription());
        assertEquals(2.0, simpleCoffee.getCost(), 0.01);
    }

    @Test
    void testCoffeeWithMilk() {
        Coffee coffee = new SimpleCoffee();
        coffee = new MilkDecorator(coffee);
        
        assertEquals("Simple coffee, milk", coffee.getDescription());
        assertEquals(2.5, coffee.getCost(), 0.01);
    }

    @Test
    void testCoffeeWithSugar() {
        Coffee coffee = new SimpleCoffee();
        coffee = new SugarDecorator(coffee);
        
        assertEquals("Simple coffee, sugar", coffee.getDescription());
        assertEquals(2.2, coffee.getCost(), 0.01);
    }

    @Test
    void testCoffeeWithMilkAndSugar() {
        Coffee coffee = coffeeService.createCoffeeWithMilkAndSugar();
        
        assertEquals("Simple coffee, milk, sugar", coffee.getDescription());
        assertEquals(2.7, coffee.getCost(), 0.01);
    }

    @Test
    void testCoffeeWithSugarAndMilk() {
        Coffee coffee = new SimpleCoffee();
        coffee = new SugarDecorator(coffee);
        coffee = new MilkDecorator(coffee);
        
        assertEquals("Simple coffee, sugar, milk", coffee.getDescription());
        assertEquals(2.7, coffee.getCost(), 0.01);
    }

    @Test
    void testMultipleMilkDecorators() {
        Coffee coffee = new SimpleCoffee();
        coffee = new MilkDecorator(coffee);
        coffee = new MilkDecorator(coffee);
        
        assertEquals("Simple coffee, milk, milk", coffee.getDescription());
        assertEquals(3.0, coffee.getCost(), 0.01);
    }

    @Test
    void testMultipleSugarDecorators() {
        Coffee coffee = new SimpleCoffee();
        coffee = new SugarDecorator(coffee);
        coffee = new SugarDecorator(coffee);
        
        assertEquals("Simple coffee, sugar, sugar", coffee.getDescription());
        assertEquals(2.4, coffee.getCost(), 0.01);
    }

    @Test
    void testComplexCoffeeDecorations() {
        Coffee coffee = new SimpleCoffee();
        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);
        coffee = new MilkDecorator(coffee);
        coffee = new SugarDecorator(coffee);
        
        assertEquals("Simple coffee, milk, sugar, milk, sugar", coffee.getDescription());
        assertEquals(3.4, coffee.getCost(), 0.01);
    }

    @Test
    void testDecoratorChaining() {
        // Test that decorators can be chained in any order
        Coffee coffee1 = new MilkDecorator(new SugarDecorator(new SimpleCoffee()));
        Coffee coffee2 = new SugarDecorator(new MilkDecorator(new SimpleCoffee()));
        
        // Both should have same cost but different descriptions
        assertEquals(2.7, coffee1.getCost(), 0.01);
        assertEquals(2.7, coffee2.getCost(), 0.01);
        
        assertEquals("Simple coffee, sugar, milk", coffee1.getDescription());
        assertEquals("Simple coffee, milk, sugar", coffee2.getDescription());
    }

    @Test
    void testDecoratorWithNullCoffee() {
        assertThrows(NullPointerException.class, () -> {
            new MilkDecorator(null);
        }, "MilkDecorator should throw exception with null coffee");
        
        assertThrows(NullPointerException.class, () -> {
            new SugarDecorator(null);
        }, "SugarDecorator should throw exception with null coffee");
    }

    @Test
    void testCostAccuracy() {
        Coffee coffee = new SimpleCoffee();
        double expectedCost = 2.0;
        
        assertEquals(expectedCost, coffee.getCost(), 0.001);
        
        coffee = new MilkDecorator(coffee);
        expectedCost += 0.5;
        assertEquals(expectedCost, coffee.getCost(), 0.001);
        
        coffee = new SugarDecorator(coffee);
        expectedCost += 0.2;
        assertEquals(expectedCost, coffee.getCost(), 0.001);
    }

    @Test
    void testServiceCreatedCoffeeProperties() {
        Coffee coffee = coffeeService.createCoffeeWithMilkAndSugar();
        
        assertNotNull(coffee);
        assertTrue(coffee.getDescription().contains("Simple coffee"));
        assertTrue(coffee.getDescription().contains("milk"));
        assertTrue(coffee.getDescription().contains("sugar"));
        assertTrue(coffee.getCost() > 2.0);
    }
}
