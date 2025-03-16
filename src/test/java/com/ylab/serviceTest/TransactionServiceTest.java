package com.ylab.serviceTest;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;
import com.ylab.repository.TransactionRepository;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TransactionServiceTest {

    @Mock
    private TransactionRepository transactionRepository;

    @InjectMocks
    private TransactionService transactionService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User("test@example.com", "testUser", "password123", false);
    }

    @Test
    void testCreateTransactionSuccess() throws SQLException {
        transactionService.createTransaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());

        verify(transactionRepository).save(argThat(transaction ->
                transaction.getAmount() == 100.0 &&
                        transaction.getDescription().equals("Salary") &&
                        transaction.getCategory().equals("Income") &&
                        transaction.getDate().equals(LocalDate.of(2025, 1, 1)) &&
                        transaction.getType() == TransactionType.INCOME &&
                        transaction.getEmail().equals(user.getEmail())));
    }

    @Test
    void testCreateTransactionInvalidAmount() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                transactionService.createTransaction(-100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail()));
        assertEquals("Сумма транзакций должна быть положительной", exception.getMessage());
    }

    @Test
    void testDeleteTransactionSuccess() throws SQLException {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        transaction.setId(1);
        when(transactionRepository.findById(1)).thenReturn(transaction);

        transactionService.deleteTransaction(1, user);
        verify(transactionRepository).deleteById(1);
    }

    @Test
    void testEditTransactionSuccess() throws SQLException {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        transaction.setId(1);
        when(transactionRepository.findById(1)).thenReturn(transaction);

        transactionService.editTransaction(1, user, 200.0, "Bonus", "Extra", LocalDate.of(2025, 1, 2), TransactionType.EXPENSE);
        verify(transactionRepository).save(transaction);
        assertEquals(200.0, transaction.getAmount());
        assertEquals("Bonus", transaction.getDescription());
        assertEquals("Extra", transaction.getCategory());
        assertEquals(LocalDate.of(2025, 1, 2), transaction.getDate());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
    }

    @Test
    void testGetUserTransactions() throws SQLException {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        when(transactionRepository.findByUser(user)).thenReturn(Collections.singletonList(transaction));

        List<Transaction> transactions = transactionService.getUserTransaction(user, user, null, null, null, null);
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));
    }
}