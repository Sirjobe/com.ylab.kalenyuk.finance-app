package com.ylab.entityTest;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class TransactionTest {

    @Test
    void testValidTransactionCreation() {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, "test@example.com");
        assertEquals(100.0, transaction.getAmount());
        assertEquals("Salary", transaction.getDescription());
        assertEquals("Income", transaction.getCategory());
        assertEquals(TransactionType.INCOME, transaction.getType());
        assertEquals("test@example.com", transaction.getUserEmail());
        assertEquals(1, transaction.getId()); // Проблема с генерацией ID
    }

    @Test
    void testSetters() {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, "test@example.com");
        transaction.setAmount(200.0);
        transaction.setDescription("Bonus");
        transaction.setCategory("Extra");
        transaction.setDate(LocalDate.of(2023, 2, 1));
        transaction.setType(TransactionType.EXPENSE);

        assertEquals(200.0, transaction.getAmount());
        assertEquals("Bonus", transaction.getDescription());
        assertEquals("Extra", transaction.getCategory());
        assertEquals(LocalDate.of(2023, 2, 1), transaction.getDate());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
    }
}