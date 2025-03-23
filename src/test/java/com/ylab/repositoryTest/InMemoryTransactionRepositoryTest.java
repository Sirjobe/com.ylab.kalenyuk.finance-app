package com.ylab.repositoryTest;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;
import com.ylab.repository.InMemoryTransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryTransactionRepositoryTest {

    private InMemoryTransactionRepository repository;
    private User user;

    @BeforeEach
    void setUp() {
        repository = new InMemoryTransactionRepository();
        user = new User("test@example.com", "testUser", "password123", false);
    }

    @Test
    void testSaveAndFindById() {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail());
        repository.save(transaction);
        Transaction found = repository.findById(1);
        assertNotNull(found);
        assertEquals(100.0, found.getAmount());
        assertEquals("Salary", found.getDescription());
    }

    @Test
    void testFindByUser() {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail());
        repository.save(transaction);
        List<Transaction> transactions = repository.findByUser(user);
        assertEquals(1, transactions.size());
        assertEquals(transaction, transactions.get(0));
    }

    @Test
    void testDeleteById() {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail());
        repository.save(transaction);
        repository.deleteById(1);
        assertNull(repository.findById(1));
    }

    @Test
    void testUpdateTransaction() {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2023, 1, 1), TransactionType.INCOME, user.getEmail());
        repository.save(transaction);
        Transaction updatedTransaction = new Transaction(200.0, "Bonus", "Extra", LocalDate.of(2023, 1, 2), TransactionType.INCOME, user.getEmail());
        repository.save(updatedTransaction); // ID остается 1 из-за бага
        Transaction found = repository.findById(1);
        assertEquals(200.0, found.getAmount());
    }
}