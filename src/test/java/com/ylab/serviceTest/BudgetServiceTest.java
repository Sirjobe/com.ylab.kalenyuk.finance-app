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
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import java.time.LocalDate;
import java.util.Collections;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
        MockitoAnnotations.openMocks(this);
        user = new User("test@example.com", "testUser", "password123", false);
    }

    @Test
    void testSetMonthlyBudget() {
        budgetService.setMonthlyBudget(user, user, 1000.0, 2023, 1);
        verify(budgetRepository).save(any(Budget.class));
    }

    @Test
    void testCheckBudgetExceed() {
        Budget budget = new Budget(1000.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), user.getEmail());
        when(budgetRepository.findById(1)).thenReturn(budget);
        when(transactionService.getUserTransaction(user, user, budget.getStart(), budget.getEnd(), null, TransactionType.EXPENSE))
                .thenReturn(Collections.singletonList(new Transaction(1500.0, "Test", "Food", LocalDate.of(2023, 1, 15), TransactionType.EXPENSE, user.getEmail())));

        String result = budgetService.checkBudget(1, user, user);
        assertTrue(result.contains("Бюджет превышен"));
    }
}