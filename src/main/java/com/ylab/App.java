package com.ylab;

import com.ylab.repository.*;
import com.ylab.repository.impl.JdbcBudgetRepository;
import com.ylab.repository.impl.JdbcGoalRepository;
import com.ylab.repository.impl.JdbcTransactionRepository;
import com.ylab.repository.impl.JdbcUserRepository;
import com.ylab.service.*;
import liquibase.Contexts;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;

import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.Scanner;

public class App {

    public static void main(String[] args) throws Exception {
        Properties props = new Properties();
        try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")) {
            props.load(fis);
        }
        boolean connected = false;
        int retries = 5;
        Connection conn = null;
        while (!connected && retries > 0) {
            try {
                conn = DriverManager.getConnection(
                        props.getProperty("db.url"),
                        props.getProperty("db.username"),
                        props.getProperty("db.password"));
                System.out.println("Подключение к базе успешно!");
                Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(conn));
                Liquibase liquibase = new Liquibase(props.getProperty("liquibase.changelog"), new ClassLoaderResourceAccessor(), database);
                liquibase.update(new Contexts());
                connected = true;
            } catch (SQLException e) {
                System.out.println("База ещё не готова, повтор через 2 секунды... (" + retries + " осталось)");
                Thread.sleep(2000);
                retries--;
            }
        }
        if (!connected) {
            throw new RuntimeException("Не удалось подключиться к базе после нескольких попыток");
        }
        /**
        *
        * Инициализация репозиториев
        *
         */
        UserRepository userRepository = new JdbcUserRepository(props);
        TransactionRepository transactionRepository = new JdbcTransactionRepository(props);
        BudgetRepository budgetRepository = new JdbcBudgetRepository(props);
        GoalRepository goalRepository = new JdbcGoalRepository(props);

        /**
         *
         * Инициализация сервисов
         *
         */
        UserService userService = new UserService(userRepository);
        TransactionService transactionService = new TransactionService(transactionRepository);
        BudgetService budgetService = new BudgetService(budgetRepository, transactionService);
        StatisticsService statisticsService = new StatisticsService(transactionService);
        GoalService goalService = new GoalService(goalRepository, statisticsService);
        EmailSender emailSender = new ConsoleEmailSender();
        NotificationService notificationService = new NotificationService(budgetService, goalService, emailSender, transactionService);
        transactionService.setNotificationService(notificationService);
        transactionService.setBudgetService(budgetService);

        /**
         *
         * Создание и запуск меню
         *
         */
        Menu menu = new Menu(userService, transactionService, budgetService, statisticsService, goalService, notificationService);
        menu.run();
    }
}