package com.ylab.repositoryTest.impTest;

import com.ylab.entity.Goal;
import com.ylab.entity.User;
import com.ylab.repository.impl.JdbcGoalRepository;
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
public class JdbcGoalRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("finance_db")
            .withUsername("finance_user")
            .withPassword("secure_password");

    private JdbcGoalRepository repository;
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
                CREATE TABLE IF NOT EXISTS finance.goals (
                    id SERIAL PRIMARY KEY,
                    target_amount DOUBLE PRECISION NOT NULL,
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    description VARCHAR(255) NOT NULL,
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
            stmt.execute("TRUNCATE TABLE finance.goals RESTART IDENTITY"); // Очистка таблицы goals
            stmt.execute("TRUNCATE TABLE finance.users RESTART IDENTITY"); // Очистка таблицы users (если используется)
        }

        repository = new JdbcGoalRepository(dbProperties);
        user = new User("test@example.com", "testUser", "password123", false);
    }

    @Test
    void testSaveAndFindById() throws SQLException {
        Goal goal = new Goal(500.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Vacation", user.getEmail());
        repository.save(goal);
        Goal found = repository.findById(goal.getId());
        assertNotNull(found);
        assertEquals(500.0, found.getTargetAmount());
        assertEquals("Vacation", found.getDescription());
    }

    @Test
    void testFindByUser() throws SQLException {
        Goal goal1 = new Goal(500.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Vacation", user.getEmail());
        Goal goal2 = new Goal(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Car", user.getEmail());
        repository.save(goal1);
        repository.save(goal2);

        List<Goal> goals = repository.findByUser(user);
        assertEquals(2, goals.size());
        assertTrue(goals.stream().anyMatch(g -> g.getTargetAmount() == 500.0));
        assertTrue(goals.stream().anyMatch(g -> g.getTargetAmount() == 1000.0));
    }

    @Test
    void testDeleteById() throws SQLException {
        Goal goal = new Goal(500.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Vacation", user.getEmail());
        repository.save(goal);
        repository.deleteById(goal.getId());
        assertNull(repository.findById(goal.getId()));
    }
}