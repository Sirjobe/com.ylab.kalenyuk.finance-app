package com.ylab.serviceTest;

import com.ylab.entity.User;
import com.ylab.repository.UserRepository;
import com.ylab.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @BeforeEach
    void setUp() {
        // Пустой метод, так как нет необходимости в дополнительной инициализации
    }

    @Test
    void testRegistrationSuccess() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        userService.registration("test@example.com", "testUser", "password123");
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("test@example.com") &&
                        user.getUsername().equals("testUser") &&
                        user.getPassword().equals("password123") &&
                        !user.isAdmin() &&
                        !user.isBlocked()));
    }

    @Test
    void testRegistrationEmailTaken() {
        User existingUser = new User("test@example.com", "existing", "pass", false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(existingUser);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.registration("test@example.com", "testUser", "password123"));
        assertEquals("Пользователь с таким email уже существует!", exception.getMessage());
    }

    @Test
    void testLoginSuccess() {
        User user = new User("test@example.com", "testUser", "password123", false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        User result = userService.login("test@example.com", "password123");
        assertEquals(user, result);
    }

    @Test
    void testLoginInvalidPassword() {
        User user = new User("test@example.com", "testUser", "password123", false);
        when(userRepository.findByEmail("test@example.com")).thenReturn(user);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.login("test@example.com", "wrongpass"));
        assertEquals("Неверный email или пароль", exception.getMessage());
    }

    @Test
    void testLoginUserNotFound() {
        when(userRepository.findByEmail("test@example.com")).thenReturn(null);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                userService.login("test@example.com", "password123"));
        assertEquals("Неверный email или пароль", exception.getMessage());
    }
}