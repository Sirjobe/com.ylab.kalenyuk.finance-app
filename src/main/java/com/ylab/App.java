package com.ylab;

import com.ylab.entity.User;
import com.ylab.management.*;

import java.util.InputMismatchException;
import java.util.Scanner;

public class App {
    private static final String LINE_SEPARATOR = "------------------------";
    private static final String ROOT_MENU = "Выберите пункт меню:\n" +
            "1. Главное меню\n" +
            "2. Меню пользователя\n" +
            "3. Администрирование\n" +
            "0. Выход";
    private static final String TOP_MENU = "Выберите пункт меню:\n" +
            "1. Регистрация\n" +
            "2. Вход\n" +
            "3. Выход";
    private static final String USER_MENU = "Выберите пункт меню:\n" +
            "1. Профиль\n" +
            "2. Финансы\n" +
            "3. Бюджет\n" +
            "4. Цели\n" +
            "5. Статистика\n" +
            "%s" +
            "%d. Выход";
    private static final String EDIT_MENU = "Выберите пункт меню:\n" +
            "1. Изменить имя\n" +
            "2. Изменить пароль\n" +
            "3. Удалить аккаунт\n" +
            "0. Назад";
    private static final String TRANSACTIONS_MENU = "Выберите пункт меню:\n" +
            "1. Добавить\n" +
            "2. Редактировать\n" +
            "3. Удалить\n" +
            "4. Просмотр\n" +
            "5. Уведомления\n" +
            "0. Назад";

    public static void main(String[] args) {
        UserManager userManager = new UserManager();
        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayMenu(ROOT_MENU);
            int choice = getUserChoice(scanner);
            if (choice == 0) break;

            switch (choice) {
                case 1 -> handleTopMenu(userManager, scanner);
                case 2 -> handleUserMenu(userManager, scanner);
                case 3 -> handleAdminMenu(userManager, scanner);
                default -> System.out.println("Неверный выбор");
            }
        }
        scanner.close();
        System.out.println("До встречи!");
    }

    // Отображение меню и получение выбора пользователя
    private static void displayMenu(String menu) {
        System.out.println(LINE_SEPARATOR);
        System.out.println(menu);
        System.out.println(LINE_SEPARATOR);
    }

    private static int getUserChoice(Scanner scanner) {
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера
            return choice;
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: введите число");
            scanner.nextLine();
            return -1; // Неверный ввод
        }
    }

    // Обработка главного меню (регистрация и вход)
    protected static void handleTopMenu(UserManager userManager, Scanner scanner) {
        displayMenu(TOP_MENU);
        int choice = getUserChoice(scanner);
        switch (choice) {
            case 1 -> {
                System.out.println("Регистрация:");
                userManager.registration();
                if (userManager.getCurrentUser() != null) {
                    showUserMenu(userManager, scanner);
                }
            }
            case 2 -> {
                System.out.println("Вход:");
                userManager.authentication();
                if (userManager.getCurrentUser() != null) {
                    showUserMenu(userManager, scanner);
                }
            }
            case 3 -> System.exit(0);
            default -> System.out.println("Неверный выбор");
        }
    }

    // Обработка пользовательского меню
    protected static void handleUserMenu(UserManager userManager, Scanner scanner) {
        if (userManager.getCurrentUser() == null) {
            System.out.println("Сначала войдите в систему");
            return;
        }
        showUserMenu(userManager, scanner);
    }

    // Обработка меню администрирования
    protected static void handleAdminMenu(UserManager userManager, Scanner scanner) {
        if (userManager.getCurrentUser() == null) {
            System.out.println("Сначала войдите в систему");
            return;
        }
        TransactionManager transactionManager = new TransactionManager(userManager.getCurrentUser());
        AdminManager adminManager = new AdminManager(userManager, transactionManager, userManager.getCurrentUser());
        adminManager.manageAdminFunctions();
    }

    // Пользовательское меню
    protected static void showUserMenu(UserManager userManager, Scanner scanner) {
        TransactionManager transactionManager = new TransactionManager(userManager.getCurrentUser());
        BudgetManager budgetManager = new BudgetManager(userManager.getCurrentUser(), transactionManager);
        GoalManager goalManager = new GoalManager(userManager.getCurrentUser(), transactionManager);
        StatisticsManager statisticsManager = new StatisticsManager(userManager.getCurrentUser(), transactionManager);
        NotificationManager notificationManager = new NotificationManager(userManager.getCurrentUser(), transactionManager);
        AdminManager adminManager = new AdminManager(userManager, transactionManager, userManager.getCurrentUser());

        User currentUser = userManager.getCurrentUser();
        boolean isAdmin = currentUser.isAdmin();
        String adminOption = isAdmin ? "6. Администрирование\n" : "";
        int exitOption = isAdmin ? 7 : 6;
        String formattedUserMenu = String.format(USER_MENU, adminOption, exitOption);

        while (true) {
            System.out.println("Добро пожаловать, " + currentUser.getUsername() + "!");
            displayMenu(formattedUserMenu);
            int choice = getUserChoice(scanner);
            if (choice == exitOption) break;

            switch (choice) {
                case 1 -> handleProfileMenu(userManager, scanner);
                case 2 -> handleTransactionsMenu(transactionManager, notificationManager, scanner);
                case 3 -> budgetManager.manageBudget();
                case 4 -> goalManager.manageGoals();
                case 5 -> statisticsManager.showStatistics();
                case 6 -> {
                    if (isAdmin) adminManager.manageAdminFunctions();
                    else System.out.println("Неверный выбор");
                }
                default -> System.out.println("Неверный выбор");
            }
        }
    }

    // Меню профиля
    protected static void handleProfileMenu(UserManager userManager, Scanner scanner) {
        User currentUser = userManager.getCurrentUser();
        System.out.println("Профиль:");
        System.out.println("Email: " + currentUser.getEmail());
        System.out.println("Имя: " + currentUser.getUsername());
        displayMenu(EDIT_MENU);
        int choice = getUserChoice(scanner);

        switch (choice) {
            case 0 -> {}
            case 1 -> userManager.editNameUser(currentUser.getEmail());
            case 2 -> userManager.editPasswordUser(currentUser.getEmail());
            case 3 -> {
                userManager.removeUser(currentUser.getEmail());
                if (userManager.getCurrentUser() == null) return;
            }
            default -> System.out.println("Неверный выбор");
        }
    }

    // Меню транзакций
    protected static void handleTransactionsMenu(TransactionManager transactionManager,
                                               NotificationManager notificationManager,
                                               Scanner scanner) {
        displayMenu(TRANSACTIONS_MENU);
        int choice = getUserChoice(scanner);

        switch (choice) {
            case 0 -> {}
            case 1 -> transactionManager.addTransaction();
            case 2 -> transactionManager.editTransaction();
            case 3 -> transactionManager.removeTransaction();
            case 4 -> transactionManager.viewTransaction();
            case 5 -> notificationManager.manageNotifications();
            default -> System.out.println("Неверный выбор");
        }
    }
}