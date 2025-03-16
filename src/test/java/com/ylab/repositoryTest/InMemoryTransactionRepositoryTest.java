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
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        repository.save(transaction);
        assertEquals(1, transaction.getId());

        Transaction found = repository.findById(1);
        assertNotNull(found);
        assertEquals(100.0, found.getAmount());
        assertEquals("Salary", found.getDescription());
    }


    @Test
    void testFindByIdNotFound() {
        Transaction found = repository.findById(999);
        assertNull(found);
    }

    @Test
    void testFindByUser() {
        Transaction transaction1 = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        Transaction transaction2 = new Transaction(200.0, "Food", "Expense", LocalDate.of(2025, 1, 2), TransactionType.EXPENSE, user.getEmail());
        repository.save(transaction1);
        repository.save(transaction2);

        List<Transaction> transactions = repository.findByUser(user);
        assertEquals(2, transactions.size());
        assertTrue(transactions.contains(transaction1));
        assertTrue(transactions.contains(transaction2));
    }

    @Test
    void testDeleteById() {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        repository.save(transaction);
        repository.deleteById(transaction.getId());

        Transaction found = repository.findById(transaction.getId());
        assertNull(found);
    }

    @Test
    void testDeleteByIdNotFound() {
        repository.deleteById(999); // Удаление несуществующего ID не должно вызывать ошибку
        assertNull(repository.findById(999));
    }
}