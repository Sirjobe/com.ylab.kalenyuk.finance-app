package com.ylab.entityTest;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    void testValidTransactionCreation() {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, "test@example.com");
        assertEquals(100.0, transaction.getAmount());
        assertEquals("Salary", transaction.getDescription());
        assertEquals("Income", transaction.getCategory());
        assertEquals(LocalDate.of(2025, 1, 1), transaction.getDate());
        assertEquals(TransactionType.INCOME, transaction.getType());
        assertEquals("test@example.com", transaction.getEmail());
        assertEquals(0, transaction.getId()); // ID по умолчанию 0
    }

    @Test
    void testSettersAndGetters() {
        Transaction transaction = new Transaction();
        transaction.setId(1);
        transaction.setAmount(200.0);
        transaction.setDescription("Bonus");
        transaction.setCategory("Extra");
        transaction.setDate(LocalDate.of(2025, 2, 1));
        transaction.setType(TransactionType.EXPENSE);
        transaction.setEmail("new@example.com");

        assertEquals(1, transaction.getId());
        assertEquals(200.0, transaction.getAmount());
        assertEquals("Bonus", transaction.getDescription());
        assertEquals("Extra", transaction.getCategory());
        assertEquals(LocalDate.of(2025, 2, 1), transaction.getDate());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals("new@example.com", transaction.getEmail());
    }
}