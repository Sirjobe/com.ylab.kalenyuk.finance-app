package com.ylab;

import com.ylab.entity.User;
import com.ylab.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MenuTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    @Mock private UserService userService;
    @Mock private TransactionService transactionService;
    @Mock private BudgetService budgetService;
    @Mock private StatisticsService statisticsService;
    @Mock private GoalService goalService;
    @Mock private NotificationService notificationService;

    private Menu menu;
    private User testUser;

    @BeforeEach
    void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));
        testUser = new User("test@example.com", "user", "pass", false);
        menu = new Menu(
                userService,
                transactionService,
                budgetService,
                statisticsService,
                goalService,
                notificationService
        );
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    void testRegistrationSuccess() throws Exception {
        String input = "test@example.com\nuser\npass\n";
        provideInput(input);

        Method registerMethod = Menu.class.getDeclaredMethod("register");
        registerMethod.setAccessible(true);

        registerMethod.invoke(menu);

        verify(userService).registration(eq("test@example.com"), eq("user"), eq("pass"));
        assertTrue(outContent.toString().contains("Регистрация успешна"));
    }

    @Test
    void testLoginSuccess() throws Exception {
        when(userService.login("test@example.com", "pass")).thenReturn(testUser);

        String input = "test@example.com\npass\n";
        provideInput(input);

        Method loginMethod = Menu.class.getDeclaredMethod("login");
        loginMethod.setAccessible(true);

        loginMethod.invoke(menu);

        Field currentUserField = Menu.class.getDeclaredField("currentUser");
        currentUserField.setAccessible(true);
        User currentUser = (User) currentUserField.get(menu);

        assertEquals(testUser.getEmail(), currentUser.getEmail());
        assertTrue(outContent.toString().contains("Вход успешен"));
    }

    @Test
    void testViewStatistics() throws Exception {
        setCurrentUser(testUser);
        when(statisticsService.calculateCurrentBalance(testUser, testUser)).thenReturn(5000.0);

        String input = "1\n3\n";
        provideInput(input);
        invokePrivateMethod("viewStatistics");

        assertTrue(outContent.toString().contains("Текущий баланс: 5000.0"));
    }

    private void setCurrentUser(User user) throws Exception {
        Field field = Menu.class.getDeclaredField("currentUser");
        field.setAccessible(true);
        field.set(menu, user);
    }

    private void provideInput(String data) throws Exception {
        ByteArrayInputStream testIn = new ByteArrayInputStream(data.getBytes());
        Field scannerField = Menu.class.getDeclaredField("scanner");
        scannerField.setAccessible(true);
        scannerField.set(menu, new Scanner(testIn));
    }

    private void invokePrivateMethod(String methodName) throws Exception {
        Method method = Menu.class.getDeclaredMethod(methodName);
        method.setAccessible(true);
        method.invoke(menu);
    }
}