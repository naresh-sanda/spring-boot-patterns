package com.patterns.creational;

import com.patterns.creational.builder.User;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BuilderPatternTest {

    @Test
    void testCompleteUserBuilding() {
        User user = new User.UserBuilder()
                .firstName("John")
                .lastName("Doe")
                .email("john.doe@example.com")
                .phone("+1234567890")
                .address("123 Main St, City, State")
                .age(30)
                .build();

        assertEquals("John", user.getFirstName());
        assertEquals("Doe", user.getLastName());
        assertEquals("john.doe@example.com", user.getEmail());
        assertEquals("+1234567890", user.getPhone());
        assertEquals("123 Main St, City, State", user.getAddress());
        assertEquals(30, user.getAge());
    }

    @Test
    void testPartialUserBuilding() {
        User user = new User.UserBuilder()
                .firstName("Jane")
                .email("jane@example.com")
                .build();

        assertEquals("Jane", user.getFirstName());
        assertEquals("jane@example.com", user.getEmail());
        assertNull(user.getLastName());
        assertNull(user.getPhone());
        assertNull(user.getAddress());
        assertEquals(0, user.getAge()); // Default int value
    }

    @Test
    void testMinimalUserBuilding() {
        User user = new User.UserBuilder().build();

        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNull(user.getPhone());
        assertNull(user.getAddress());
        assertEquals(0, user.getAge());
    }

    @Test
    void testBuilderChaining() {
        User.UserBuilder builder = new User.UserBuilder();
        
        // Test that each method returns the builder for chaining
        assertSame(builder, builder.firstName("Test"));
        assertSame(builder, builder.lastName("User"));
        assertSame(builder, builder.email("test@example.com"));
        assertSame(builder, builder.phone("123456789"));
        assertSame(builder, builder.address("Test Address"));
        assertSame(builder, builder.age(25));
    }

    @Test
    void testBuilderReusability() {
        User.UserBuilder builder = new User.UserBuilder()
                .firstName("John")
                .lastName("Doe");

        User user1 = builder.email("john1@example.com").build();
        User user2 = builder.email("john2@example.com").build();

        assertEquals("John", user1.getFirstName());
        assertEquals("John", user2.getFirstName());
        assertEquals("john2@example.com", user1.getEmail()); // Builder state is shared
        assertEquals("john2@example.com", user2.getEmail());
    }

    @Test
    void testUserToString() {
        User user = new User.UserBuilder()
                .firstName("Test")
                .lastName("User")
                .email("test@example.com")
                .age(25)
                .build();

        String userString = user.toString();
        
        assertTrue(userString.contains("Test"));
        assertTrue(userString.contains("User"));
        assertTrue(userString.contains("test@example.com"));
        assertTrue(userString.contains("25"));
    }

    @Test
    void testNullValues() {
        User user = new User.UserBuilder()
                .firstName(null)
                .lastName(null)
                .email(null)
                .phone(null)
                .address(null)
                .build();

        assertNull(user.getFirstName());
        assertNull(user.getLastName());
        assertNull(user.getEmail());
        assertNull(user.getPhone());
        assertNull(user.getAddress());
    }

    @Test
    void testEmptyStringValues() {
        User user = new User.UserBuilder()
                .firstName("")
                .lastName("")
                .email("")
                .phone("")
                .address("")
                .build();

        assertEquals("", user.getFirstName());
        assertEquals("", user.getLastName());
        assertEquals("", user.getEmail());
        assertEquals("", user.getPhone());
        assertEquals("", user.getAddress());
    }

    @Test
    void testNegativeAge() {
        User user = new User.UserBuilder()
                .age(-5)
                .build();

        assertEquals(-5, user.getAge());
    }

    @Test
    void testLargeAge() {
        User user = new User.UserBuilder()
                .age(150)
                .build();

        assertEquals(150, user.getAge());
    }
}
