package com.ylab.repositoryTest.impTest;

import com.ylab.entity.Budget;
import com.ylab.entity.User;
import com.ylab.repository.impl.JdbcBudgetRepository;
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
public class JdbcBudgetRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("finance_app")
            .withUsername("test_user")
            .withPassword("test_password");

    private JdbcBudgetRepository repository;
    private User user;

    @BeforeEach
    void setUp() throws SQLException {
        // Настройки подключения из Testcontainers
        Properties dbProperties = new Properties();
        dbProperties.setProperty("db.url", postgres.getJdbcUrl());
        dbProperties.setProperty("db.username", postgres.getUsername());
        dbProperties.setProperty("db.password", postgres.getPassword());

        // Инициализация репозитория
        repository = new JdbcBudgetRepository(dbProperties);
        user = new User("test@example.com", "TestUser", "password123", false);

        // Создаем схему и таблицу, очищаем данные
        try (Connection connection = DriverManager.getConnection(
                postgres.getJdbcUrl(),
                postgres.getUsername(),
                postgres.getPassword());
             Statement stmt = connection.createStatement()) {
            stmt.execute("CREATE SCHEMA IF NOT EXISTS finance");
            stmt.execute("""
                CREATE TABLE IF NOT EXISTS finance.budgets (
                    id SERIAL PRIMARY KEY,
                    limit_amount DOUBLE PRECISION NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    email VARCHAR(255) NOT NULL
                )
            """);
            stmt.execute("TRUNCATE TABLE finance.budgets RESTART IDENTITY"); // Очистка таблицы и сброс ID
        }
    }

    @Test
    void testSave() throws SQLException {
        Budget budget = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), user.getEmail());
        repository.save(budget);

        assertTrue(budget.getId() > 0);
        Budget found = repository.findById(budget.getId());
        assertNotNull(found);
        assertEquals(1000.0, found.getLimit());
        assertEquals(user.getEmail(), found.getEmail());
        assertEquals(LocalDate.of(2025, 1, 1), found.getStart());
        assertEquals(LocalDate.of(2025, 1, 31), found.getEnd());
    }

    @Test
    void testFindById() throws SQLException {
        Budget budget = new Budget(2000.0, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28), user.getEmail());
        repository.save(budget);

        Budget found = repository.findById(budget.getId());
        assertNotNull(found);
        assertEquals(budget.getId(), found.getId());
        assertEquals(2000.0, found.getLimit());
    }

    @Test
    void testFindByUser() throws SQLException {
        Budget budget1 = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), user.getEmail());
        Budget budget2 = new Budget(2000.0, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28), user.getEmail());
        repository.save(budget1);
        repository.save(budget2);

        List<Budget> budgets = repository.findByUser(user);
        assertEquals(2, budgets.size());
        assertTrue(budgets.stream().anyMatch(b -> b.getLimit() == 1000.0));
        assertTrue(budgets.stream().anyMatch(b -> b.getLimit() == 2000.0));
    }

    @Test
    void testDeleteById() throws SQLException {
        Budget budget = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), user.getEmail());
        repository.save(budget);
        repository.deleteById(budget.getId());

        assertNull(repository.findById(budget.getId()));
    }
}