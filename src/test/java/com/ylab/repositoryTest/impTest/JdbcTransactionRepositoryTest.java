package com.ylab.repositoryTest.impTest;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;
import com.ylab.repository.impl.JdbcTransactionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class JdbcTransactionRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("finance_db")
            .withUsername("finance_user")
            .withPassword("secure_password");

    private JdbcTransactionRepository repository;
    private User user;

    @BeforeAll
    static void init() {
        postgres.start();
    }

    @BeforeEach
    void setUp() throws SQLException {
        Properties dbProperties = new Properties();
        dbProperties.setProperty("db.url", postgres.getJdbcUrl());
        dbProperties.setProperty("db.username", postgres.getUsername());
        dbProperties.setProperty("db.password", postgres.getPassword());

        // Создаем схему, таблицы и очищаем данные
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword());
             Statement stmt = conn.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS finance;");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS finance.transactions (
                    id SERIAL PRIMARY KEY,
                    amount DOUBLE PRECISION NOT NULL,
                    description VARCHAR(255) NOT NULL,
                    category VARCHAR(255) NOT NULL,
                    transaction_date DATE NOT NULL,
                    transaction_type VARCHAR(50) NOT NULL,
                    email VARCHAR(255) NOT NULL
                )
            """);
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS finance.users (
                    id SERIAL PRIMARY KEY,
                    email VARCHAR(255) NOT NULL UNIQUE,
                    username VARCHAR(255) NOT NULL,
                    password VARCHAR(255) NOT NULL,
                    is_admin BOOLEAN DEFAULT FALSE,
                    is_blocked BOOLEAN DEFAULT FALSE
                )
            """);
            stmt.execute("TRUNCATE TABLE finance.transactions RESTART IDENTITY"); // Очистка таблицы transactions
            stmt.execute("TRUNCATE TABLE finance.users RESTART IDENTITY"); // Очистка таблицы users (если используется)
        }

        repository = new JdbcTransactionRepository(dbProperties);
        user = new User("test@example.com", "testUser", "password123", false);
    }

    @Test
    void testSaveAndFindById() throws SQLException {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        repository.save(transaction);
        Transaction found = repository.findById(transaction.getId());
        assertNotNull(found);
        assertEquals(100.0, found.getAmount());
        assertEquals("Salary", found.getDescription());
    }

    @Test
    void testFindByUser() throws SQLException {
        Transaction transaction1 = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        Transaction transaction2 = new Transaction(50.0, "Groceries", "Expense", LocalDate.of(2025, 1, 2), TransactionType.EXPENSE, user.getEmail());
        repository.save(transaction1);
        repository.save(transaction2);

        List<Transaction> transactions = repository.findByUser(user);
        assertEquals(2, transactions.size());
        assertTrue(transactions.stream().anyMatch(t -> t.getAmount() == 100.0 && t.getType() == TransactionType.INCOME));
        assertTrue(transactions.stream().anyMatch(t -> t.getAmount() == 50.0 && t.getType() == TransactionType.EXPENSE));
    }

    @Test
    void testDeleteById() throws SQLException {
        Transaction transaction = new Transaction(100.0, "Salary", "Income", LocalDate.of(2025, 1, 1), TransactionType.INCOME, user.getEmail());
        repository.save(transaction);
        repository.deleteById(transaction.getId());
        assertNull(repository.findById(transaction.getId()));
    }
}