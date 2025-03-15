//package com.ylab.entityTest;
//
//import com.ylab.entity.User;
//import org.junit.jupiter.api.Test;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class UserTest {
//
//    @Test
//    void testValidUserCreation() {
//        User user = new User("test@example.com", "testUser", "password123", false);
//        assertEquals("test@example.com", user.getEmail());
//        assertEquals("testUser", user.getUsername());
//        assertEquals("password123", user.getPassword());
//        assertFalse(user.isAdmin());
//        assertFalse(user.isBlocked());
//    }
//
//    @Test
//    void testInvalidEmailThrowsException() {
//        Exception exception = assertThrows(IllegalArgumentException.class, () ->
//                new User("invalid-email", "testUser", "password123", false));
//        assertEquals("Некорректный формат email", exception.getMessage());
//    }
//
//    @Test
//    void testPasswordValidation() {
//        User user = new User("test@example.com", "testUser", "pass", false);
//        assertFalse(user.isPasswordValid()); // Пароль короче 6 символов
//
//        user.setPassword("password123");
//        assertTrue(user.isPasswordValid());
//    }
//}