package com.ylab.serviceTest;

import com.ylab.entity.Budget;
import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;
import com.ylab.repository.BudgetRepository;
import com.ylab.service.BudgetService;
import com.ylab.service.TransactionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BudgetServiceTest {

    @Mock
    private BudgetRepository budgetRepository;

    @Mock
    private TransactionService transactionService;

    @InjectMocks
    private BudgetService budgetService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "testUser", "password123", false);
    }

    @Test
    void testSetMonthlyBudget() throws SQLException {
        budgetService.setMonthlyBudget(user, user, 1000.0, 2025, 1);

        verify(budgetRepository).save(argThat(budget ->
                budget.getLimit() == 1000.0 &&
                        budget.getStart().equals(LocalDate.of(2025, 1, 1)) &&
                        budget.getEnd().equals(LocalDate.of(2025, 1, 31)) &&
                        budget.getEmail().equals(user.getEmail())));
    }

    @Test
    void testCheckBudgetExceed() throws SQLException {
        Budget budget = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), user.getEmail());
        budget.setId(1);
        Transaction expense = new Transaction(1500.0, "Test", "Food", LocalDate.of(2025, 1, 15), TransactionType.EXPENSE, user.getEmail());

        when(budgetRepository.findById(1)).thenReturn(budget);
        when(transactionService.getTransactionsByUserAndPeriod(user, budget.getStart(), budget.getEnd()))
                .thenReturn(Collections.singletonList(expense));

        String result = budgetService.checkBudget(1, user, user);
        System.out.println("Result: " + result);
        assertNotNull(result);
        assertTrue(result.contains("Бюджет превышен"));
        assertTrue(result.contains("1500,00"));
        assertTrue(result.contains("1000,00"));
        assertTrue(result.contains("500,00"));
    }

    @Test
    void testCheckBudgetWithinLimit() throws SQLException {
        Budget budget = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), user.getEmail());
        budget.setId(1);
        Transaction expense = new Transaction(500.0, "Test", "Food", LocalDate.of(2025, 1, 15), TransactionType.EXPENSE, user.getEmail());

        when(budgetRepository.findById(1)).thenReturn(budget);
        when(transactionService.getTransactionsByUserAndPeriod(user, budget.getStart(), budget.getEnd()))
                .thenReturn(Collections.singletonList(expense));

        String result = budgetService.checkBudget(1, user, user);
        assertTrue(result.contains("Бюджет в пределах нормы: потрачено 500,00 из 1000,00"));
    }
}