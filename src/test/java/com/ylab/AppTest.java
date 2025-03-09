package com.ylab;

import com.ylab.entity.User;
import com.ylab.management.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final PrintStream originalErr = System.err;

    @Mock
    private UserManager userManager;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(outContent));
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setErr(originalErr);
        outContent.reset();
    }

    @Test
    public void testMainExit() {
        String input = "0\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        App.main(new String[]{});

        String output = outContent.toString();
        assertTrue(output.contains("До встречи!"), "Приложение должно завершиться с прощальным сообщением");
    }

    @Test
    public void testHandleTopMenuRegistration() {
        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        App.handleTopMenu(userManager, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Регистрация:"), "Должно отобразиться приглашение к регистрации");
        verify(userManager, times(1)).registration();
    }

    @Test
    public void testHandleTopMenuAuthentication() {
        String input = "2\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        App.handleTopMenu(userManager, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Вход:"), "Должно отобразиться приглашение ко входу");
        verify(userManager, times(1)).authentication();
    }

    @Test
    public void testHandleUserMenuWithoutAuthorization() {
        when(userManager.getCurrentUser()).thenReturn(null);

        String input = "";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        App.handleUserMenu(userManager, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Сначала войдите в систему"), "Должно быть сообщение об отсутствии авторизации");
    }

    @Test
    public void testHandleAdminMenuWithoutAuthorization() {
        when(userManager.getCurrentUser()).thenReturn(null);

        String input = "";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        App.handleAdminMenu(userManager, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Сначала войдите в систему"), "Должно быть сообщение об отсутствии авторизации");
    }

    @Test
    public void testShowUserMenuProfile() {
        User user = new User("test@example.com", "testUser", "password");
        when(userManager.getCurrentUser()).thenReturn(user);

        String input = "1\n0\n6\n"; // Выбор профиля, возврат, выход
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        App.showUserMenu(userManager, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Добро пожаловать, testUser!"), "Должно быть приветствие");
        assertTrue(output.contains("Профиль:"), "Должно отобразиться меню профиля");
    }

    @Test
    public void testHandleProfileMenuEditName() {
        User user = new User("test@example.com", "testUser", "password");
        when(userManager.getCurrentUser()).thenReturn(user);

        String input = "1\nnewName\n"; // Выбор "Изменить имя" и ввод нового имени
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        // Переопределяем поведение editNameUser, чтобы использовать переданный Scanner
        doAnswer(invocation -> {
            System.out.println("Введите новое имя пользователя: ");
            String name = scanner.nextLine(); // Используем тот же Scanner
            if (!name.isEmpty() && !user.getUsername().equals(name)) {
                user.setUsername(name);
                System.out.println("Имя изменено");
            }
            return null;
        }).when(userManager).editNameUser("test@example.com");

        App.handleProfileMenu(userManager, scanner);

        String output = outContent.toString();
        assertTrue(output.contains("Введите новое имя пользователя:"), "Должно быть приглашение к изменению имени");
        verify(userManager, times(1)).editNameUser("test@example.com");
    }

    @Test
    public void testHandleTransactionsMenuAdd() {
        TransactionManager transactionManager = mock(TransactionManager.class);
        NotificationManager notificationManager = mock(NotificationManager.class);

        String input = "1\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));
        Scanner scanner = new Scanner(System.in);

        App.handleTransactionsMenu(transactionManager, notificationManager, scanner);

        verify(transactionManager, times(1)).addTransaction();
    }
}