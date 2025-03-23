package com.ylab.repositoryTest;

import com.ylab.entity.User;
import com.ylab.repository.impl.JdbcUserRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
public class JdbcUserRepositoryTest {

    @Container
    private static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15")
            .withDatabaseName("finance_db")
            .withUsername("finance_user")
            .withPassword("secure_password");

    private static JdbcUserRepository repository;
    private static Properties dbProperties;

    @BeforeAll
    static void init() throws SQLException {
        // Запускаем контейнер один раз перед всеми тестами
        postgres.start();

        // Настраиваем свойства подключения
        dbProperties = new Properties();
        dbProperties.setProperty("db.url", postgres.getJdbcUrl());
        dbProperties.setProperty("db.username", postgres.getUsername());
        dbProperties.setProperty("db.password", postgres.getPassword());

        // Создаем схему и таблицу один раз перед всеми тестами
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            conn.createStatement().execute("CREATE SCHEMA IF NOT EXISTS finance;");
            conn.createStatement().execute(
                    "CREATE TABLE IF NOT EXISTS finance.users (" +
                            "id SERIAL PRIMARY KEY, " +
                            "email VARCHAR(255) NOT NULL UNIQUE, " +
                            "username VARCHAR(255) NOT NULL, " +
                            "password VARCHAR(255) NOT NULL, " +
                            "is_admin BOOLEAN DEFAULT FALSE, " +
                            "is_blocked BOOLEAN DEFAULT FALSE);"
            );
        }

        // Инициализируем репозиторий один раз
        repository = new JdbcUserRepository(dbProperties);
    }

    @BeforeEach
    void setUp() throws SQLException {
        // Очищаем таблицу перед каждым тестом для изоляции
        try (Connection conn = DriverManager.getConnection(postgres.getJdbcUrl(), postgres.getUsername(), postgres.getPassword())) {
            conn.createStatement().execute("TRUNCATE TABLE finance.users RESTART IDENTITY CASCADE;");
        }
    }

    @Test
    void testSaveAndFindByEmail() throws SQLException {
        User user = new User("test@example.com", "testUser", "password123", false);
        repository.save(user);
        User found = repository.findByEmail("test@example.com");
        assertNotNull(found);
        assertEquals("testUser", found.getUsername());
    }

    @Test
    void testDeleteByEmail() throws SQLException {
        User user = new User("test@example.com", "testUser", "password123", false);
        repository.save(user);
        repository.deleteByEmail("test@example.com");
        assertNull(repository.findByEmail("test@example.com"));
    }

    @Test
    void testFindAll() throws SQLException {
        User user1 = new User("test1@example.com", "user1", "password123", false);
        User user2 = new User("test2@example.com", "user2", "password123", false);
        repository.save(user1);
        repository.save(user2);
        List<User> users = repository.findAll();
        assertEquals(2, users.size());
    }
}