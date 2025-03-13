package com.ylab.serviceTest;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;
import com.ylab.service.StatisticsService;
import com.ylab.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class StatisticsServiceTest {

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private StatisticsService statisticsService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("test@example.com", "testUser", "password123", false);
    }


    @Test
    void testGenerateFinancialReport() {
        Transaction income = new Transaction(1000.0, "Salary", "Income", LocalDate.now(), TransactionType.INCOME, user.getEmail());
        Transaction expense = new Transaction(400.0, "Food", "Expense", LocalDate.now(), TransactionType.EXPENSE, user.getEmail());
        when(transactionService.getUserTransaction(user, user, null, null, null, null))
                .thenReturn(Arrays.asList(income, expense));
        when(transactionService.getUserTransaction(user, user, null, null, null, TransactionType.EXPENSE))
                .thenReturn(Collections.singletonList(expense));

        String report = statisticsService.generateFinancialReport(user, user, null, null);
        assertTrue(report.contains("Текущий баланс: 600.00"), "Отчет должен содержать баланс: " + report);
        assertTrue(report.contains("Доходы: 1000.00"), "Отчет должен содержать доходы: " + report);
        assertTrue(report.contains("Расходы: 400.00"), "Отчет должен содержать расходы: " + report);
        assertTrue(report.contains("Expense: 400.00"), "Отчет должен содержать категорию расходов: " + report);
    }

    @Test
    void testCalculateCurrentBalance() {
        Transaction income = new Transaction(1000.0, "Salary", "Income", LocalDate.now(), TransactionType.INCOME, user.getEmail());
        Transaction expense = new Transaction(400.0, "Food", "Expense", LocalDate.now(), TransactionType.EXPENSE, user.getEmail());
        when(transactionService.getUserTransaction(user, user, null, null, null, null))
                .thenReturn(Arrays.asList(income, expense));
        double balance = statisticsService.calculateCurrentBalance(user, user);
        assertEquals(600.0, balance, 0.01);
    }

    @Test
    void testCalculateIncomeAndExpense() {
        Transaction income = new Transaction(1000.0, "Salary", "Income", LocalDate.now(), TransactionType.INCOME, user.getEmail());
        Transaction expense = new Transaction(400.0, "Food", "Expense", LocalDate.now(), TransactionType.EXPENSE, user.getEmail());
        when(transactionService.getUserTransaction(user, user, null, null, null, null))
                .thenReturn(Arrays.asList(income, expense));
        double[] result = statisticsService.calculateIncomeAndExpense(user, user, null, null);
        assertEquals(1000.0, result[0], 0.01);
        assertEquals(400.0, result[1], 0.01);
    }

    @Test
    void testAnalyzeExpenseByCategory() {
        Transaction expense1 = new Transaction(200.0, "Food", "Food", LocalDate.now(), TransactionType.EXPENSE, user.getEmail());
        Transaction expense2 = new Transaction(300.0, "Rent", "Housing", LocalDate.now(), TransactionType.EXPENSE, user.getEmail());
        when(transactionService.getUserTransaction(user, user, null, null, null, TransactionType.EXPENSE))
                .thenReturn(Arrays.asList(expense1, expense2));
        Map<String, Double> expenses = statisticsService.analyzeExpenseByCategory(user, user, null, null);
        assertEquals(200.0, expenses.get("Food"), 0.01);
        assertEquals(300.0, expenses.get("Housing"), 0.01);
    }
}