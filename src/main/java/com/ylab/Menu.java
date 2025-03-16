package com.ylab;

import com.ylab.entity.*;
import com.ylab.service.*;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * Класс, реализующий консольный интерфейс для управления финансами пользователя.
 * Предоставляет меню для регистрации, входа, управления транзакциями, бюджетом, целями и статистикой.
 */
public class Menu {
    private final UserService userService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final StatisticsService statisticsService;
    private final GoalService goalService;
    private final NotificationService notificationService;
    private final Scanner scanner;
    private User currentUser;
    private boolean running;

    /**
     * Конструктор класса Menu.
     *
     * @param userService         сервис для управления пользователями
     * @param transactionService  сервис для управления транзакциями
     * @param budgetService       сервис для управления бюджетом
     * @param statisticsService   сервис для получения статистики
     * @param goalService         сервис для управления целями
     * @param notificationService сервис для отправки уведомлений
     */
    public Menu(UserService userService, TransactionService transactionService, BudgetService budgetService,
                StatisticsService statisticsService, GoalService goalService, NotificationService notificationService) {
        this.userService = userService;
        this.transactionService = transactionService;
        this.budgetService = budgetService;
        this.statisticsService = statisticsService;
        this.goalService = goalService;
        this.notificationService = notificationService;
        this.scanner = new Scanner(System.in);
        this.currentUser = null;
        this.running = true;
    }

    /**
     * Запускает основной цикл меню приложения.
     * Отображает гостевое или пользовательское меню в зависимости от статуса авторизации.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    public void run() throws SQLException {
        while (running) {
            if (currentUser == null) {
                showGuestMenu();
            } else {
                showUserMenu();
            }
        }
    }

    /**
     * Метод для завершения цикла (для тестов)
     */
    public void stop() {
        running = false;
    }

    /**
     * Отображает меню для неавторизованных пользователей (гостей).
     * Позволяет зарегистрироваться, войти или выйти из приложения.
     */
    private void showGuestMenu() {
        System.out.println("1. Регистрация");
        System.out.println("2. Вход");
        System.out.println("3. Выход");
        int choice = getIntInput("Выберите действие (1-3): ", 1, 3);

        switch (choice) {
            case 1: register(); break;
            case 2: login(); break;
            case 3: System.exit(0); break;
            default: System.out.println("Неверный выбор");
        }
    }

    /**
     * Выполняет регистрацию нового пользователя.
     * Запрашивает email, имя пользователя и пароль.
     */
    private void register() {
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите имя пользователя: ");
        String username = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        try {
            userService.registration(email, username, password);
            System.out.println("Регистрация успешна");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Выполняет вход пользователя в систему.
     * Запрашивает email и пароль.
     */
    private void login() {
        System.out.print("Введите email: ");
        String email = scanner.nextLine();
        System.out.print("Введите пароль: ");
        String password = scanner.nextLine();

        try {
            currentUser = userService.login(email, password);
            System.out.println("Вход успешен");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Отображает меню для авторизованного пользователя.
     * Включает управление профилем, транзакциями, бюджетом, целями, статистикой и уведомлениями.
     * Для администраторов добавлены дополнительные опции.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void showUserMenu() throws SQLException {
        int choice = 0;
        System.out.println("1. Управление профилем");
        System.out.println("2. Управление транзакциями");
        System.out.println("3. Управление бюджетом");
        System.out.println("4. Управление целями");
        System.out.println("5. Просмотр статистики");
        System.out.println("6. Получить уведомления");
        if (currentUser.isAdmin()) {
            System.out.println("7. Управление пользователями");
            System.out.println("8. Выход");
            choice = getIntInput("Выберите действие (1-8): ", 1, 8);
        } else {
            System.out.println("7. Выход");
            choice = getIntInput("Выберите действие (1-7): ", 1, 7);
        }

        switch (choice) {
            case 1: manageProfile(); break;
            case 2: manageTransactions(); break;
            case 3: manageBudgets(); break;
            case 4: manageGoals(); break;
            case 5: viewStatistics(); break;
            case 6: getNotifications(); break;
            case 7:
                if (currentUser.isAdmin()) {
                    manageUsers();
                } else {
                    currentUser = null;
                }
                break;
            case 8:
                if (currentUser.isAdmin()) {
                    currentUser = null;
                } else {
                    System.out.println("Неверный выбор");
                }
                break;
            default: System.out.println("Неверный выбор");
        }
    }

    /**
     * Отображает меню управления профилем.
     * Позволяет редактировать профиль или удалить аккаунт.
     */
    private void manageProfile() {
        System.out.println("1. Редактировать профиль");
        System.out.println("2. Удалить аккаунт");
        System.out.println("3. Назад");
        int choice = getIntInput("Выберите действие (1-3): ", 1, 3);

        switch (choice) {
            case 1: editProfile(); break;
            case 2: deleteAccount(); break;
            case 3: return;
            default: System.out.println("Неверный выбор");
        }
    }

    /**
     * Редактирует данные профиля текущего пользователя.
     * Запрашивает новый email, имя пользователя и пароль (опционально).
     */
    private void editProfile() {
        System.out.print("Введите новый email (или Enter, чтобы оставить прежний): ");
        String newEmail = scanner.nextLine();
        System.out.print("Введите новое имя пользователя (или Enter, чтобы оставить прежнее): ");
        String newUsername = scanner.nextLine();
        System.out.print("Введите новый пароль (или Enter, чтобы оставить прежний): ");
        String newPassword = scanner.nextLine();

        try {
            userService.editUser(currentUser.getEmail(), newEmail.isEmpty() ? currentUser.getEmail() : newEmail,
                    newUsername.isEmpty() ? currentUser.getUsername() : newUsername,
                    newPassword.isEmpty() ? currentUser.getPassword() : newPassword);
            System.out.println("Профиль обновлен");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Удаляет аккаунт текущего пользователя после подтверждения.
     */
    private void deleteAccount() {
        System.out.print("Вы уверены, что хотите удалить аккаунт? (да/нет): ");
        String confirmation = scanner.nextLine();
        if (confirmation.equalsIgnoreCase("да")) {
            try {
                userService.deleteUser(currentUser.getEmail(), currentUser);
                System.out.println("Аккаунт удален");
                currentUser = null;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: " + e.getMessage());
            }
        }
    }

    /**
     * Отображает меню управления транзакциями.
     * Позволяет добавлять, редактировать, удалять, просматривать и фильтровать транзакции.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void manageTransactions() throws SQLException {
        while (true) {
            System.out.println("1. Добавить транзакцию");
            System.out.println("2. Редактировать транзакцию");
            System.out.println("3. Удалить транзакцию");
            System.out.println("4. Просмотреть транзакции");
            System.out.println("5. Отфильтровать транзакции");
            System.out.println("6. Назад");
            int choice = getIntInput("Выберите действие (1-6): ", 1, 6);

            switch (choice) {
                case 1: addTransaction(); break;
                case 2: editTransaction(); break;
                case 3: deleteTransaction(); break;
                case 4: viewTransactions(); break;
                case 5: filterTransactions(); break;
                case 6: return;
                default: System.out.println("Неверный выбор");
            }
        }
    }

    /**
     * Добавляет новую транзакцию для текущего пользователя.
     * Запрашивает сумму, описание, категорию, дату и тип транзакции.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void addTransaction() throws SQLException {
        double amount = 0;
        while (true) {
            System.out.print("Введите сумму: ");
            try {
                amount = scanner.nextDouble();
                if (amount < 0) {
                    System.out.println("Ошибка: Сумма не может быть отрицательной.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите корректное число (например, 100.50).");
                scanner.nextLine(); // Очистка буфера
            }
        }
        scanner.nextLine();

        System.out.print("Введите описание: ");
        String description = scanner.nextLine();
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine();

        LocalDate date = null;
        while (true) {
            System.out.print("Введите дату (yyyy-mm-dd): ");
            String dateInput = scanner.nextLine();
            try {
                date = LocalDate.parse(dateInput);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Некорректный формат даты. Используйте yyyy-mm-dd (например, 2025-03-15).");
            }
        }

        TransactionType type = null;
        while (true) {
            System.out.print("Введите тип (INCOME/EXPENSE): ");
            String typeInput = scanner.nextLine().toUpperCase();
            try {
                type = TransactionType.valueOf(typeInput);
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: Введите INCOME или EXPENSE.");
            }
        }

        try {
            transactionService.createTransaction(amount, description, category, date, type, currentUser.getEmail());
            System.out.println("Транзакция добавлена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Редактирует существующую транзакцию.
     * Запрашивает ID транзакции и новые данные (сумма, описание, категория, дата, тип).
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void editTransaction() throws SQLException {
        int id = 0;
        while (true) {
            System.out.print("Введите ID транзакции: ");
            try {
                id = scanner.nextInt();
                if (id < 0) {
                    System.out.println("Ошибка: ID не может быть отрицательным.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите целое число.");
                scanner.nextLine(); // Очистка буфера
            }
        }
        scanner.nextLine();

        double newAmount = 0;
        while (true) {
            System.out.print("Введите новую сумму (или 0, чтобы не менять): ");
            try {
                newAmount = scanner.nextDouble();
                if (newAmount < 0) {
                    System.out.println("Ошибка: Сумма не может быть отрицательной.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите корректное число (например, 100.50).");
                scanner.nextLine(); // Очистка буфера
            }
        }
        scanner.nextLine();

        System.out.print("Введите новое описание (или Enter, чтобы не менять): ");
        String newDescription = scanner.nextLine();
        System.out.print("Введите новую категорию (или Enter, чтобы не менять): ");
        String newCategory = scanner.nextLine();

        LocalDate newDate = null;
        while (true) {
            System.out.print("Введите новую дату (yyyy-mm-dd или Enter, чтобы не менять): ");
            String dateInput = scanner.nextLine();
            if (dateInput.isEmpty()) {
                newDate = null;
                break;
            }
            try {
                newDate = LocalDate.parse(dateInput);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Некорректный формат даты. Используйте yyyy-mm-dd (например, 2025-03-15).");
            }
        }

        TransactionType newType = null;
        while (true) {
            System.out.print("Введите новый тип (INCOME/EXPENSE или Enter, чтобы не менять): ");
            String typeInput = scanner.nextLine();
            if (typeInput.isEmpty()) {
                newType = null;
                break;
            }
            try {
                newType = TransactionType.valueOf(typeInput.toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: Введите INCOME или EXPENSE.");
            }
        }

        try {
            transactionService.editTransaction(id, currentUser, newAmount, newDescription, newCategory, newDate, newType);
            System.out.println("Транзакция обновлена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Удаляет транзакцию по-указанному ID.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void deleteTransaction() throws SQLException {
        int id = 0;
        while (true) {
            System.out.print("Введите ID транзакции: ");
            try {
                id = scanner.nextInt();
                if (id < 0) {
                    System.out.println("Ошибка: ID не может быть отрицательным.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите целое число.");
                scanner.nextLine(); // Очистка буфера
            }
        }
        scanner.nextLine();

        try {
            transactionService.deleteTransaction(id, currentUser);
            System.out.println("Транзакция удалена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Отображает список всех транзакций текущего пользователя.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void viewTransactions() throws SQLException {
        List<Transaction> transactions = transactionService.getUserTransaction(currentUser, currentUser, null, null, null, null);
        if (transactions.isEmpty()) {
            System.out.println("Нет транзакций");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    /**
     * Фильтрует и отображает транзакции текущего пользователя по заданным параметрам.
     * Запрашивает начальную и конечную даты, категорию и тип транзакции (опционально).
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void filterTransactions() throws SQLException {
        LocalDate startDate = null;
        while (true) {
            System.out.print("Введите начальную дату (yyyy-mm-dd или Enter для пропуска): ");
            String startInput = scanner.nextLine();
            if (startInput.isEmpty()) {
                startDate = null;
                break;
            }
            try {
                startDate = LocalDate.parse(startInput);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Некорректный формат даты. Используйте yyyy-mm-dd (например, 2025-03-15).");
            }
        }

        LocalDate endDate = null;
        while (true) {
            System.out.print("Введите конечную дату (yyyy-mm-dd или Enter для пропуска): ");
            String endInput = scanner.nextLine();
            if (endInput.isEmpty()) {
                endDate = null;
                break;
            }
            try {
                endDate = LocalDate.parse(endInput);
                if (startDate != null && endDate.isBefore(startDate)) {
                    System.out.println("Ошибка: Конечная дата не может быть раньше начальной.");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Некорректный формат даты. Используйте yyyy-mm-dd (например, 2025-03-15).");
            }
        }

        System.out.print("Введите категорию (или Enter для пропуска): ");
        String category = scanner.nextLine();
        if (category.isEmpty()) category = null;

        TransactionType type = null;
        while (true) {
            System.out.print("Введите тип (INCOME/EXPENSE или Enter для пропуска): ");
            String typeInput = scanner.nextLine();
            if (typeInput.isEmpty()) {
                type = null;
                break;
            }
            try {
                type = TransactionType.valueOf(typeInput.toUpperCase());
                break;
            } catch (IllegalArgumentException e) {
                System.out.println("Ошибка: Введите INCOME или EXPENSE.");
            }
        }

        List<Transaction> transactions = transactionService.getUserTransaction(
                currentUser, currentUser, startDate, endDate, category, type);
        if (transactions.isEmpty()) {
            System.out.println("Нет транзакций по заданным фильтрам");
        } else {
            transactions.forEach(System.out::println);
        }
    }

    /**
     * Отображает меню управления бюджетом.
     * Позволяет устанавливать, удалять и просматривать бюджеты.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void manageBudgets() throws SQLException {
        while (true) {
            System.out.println("1. Установить бюджет");
            System.out.println("2. Удалить бюджет");
            System.out.println("3. Просмотреть бюджеты");
            System.out.println("4. Назад");
            int choice = getIntInput("Выберите действие (1-4): ", 1, 4);

            switch (choice) {
                case 1: setBudget(); break;
                case 2: deleteBudget(); break;
                case 3: viewBudgets(); break;
                case 4: return;
                default: System.out.println("Неверный выбор");
            }
        }
    }

    /**
     * Устанавливает месячный бюджет для текущего пользователя.
     * Запрашивает лимит, год и месяц.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void setBudget() throws SQLException {
        double limit = 0;
        while (true) {
            System.out.print("Введите лимит: ");
            try {
                limit = scanner.nextDouble();
                if (limit < 0) {
                    System.out.println("Ошибка: Лимит не может быть отрицательным.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите корректное число (например, 1000.00).");
                scanner.nextLine(); // Очистка буфера
            }
        }

        int year = 0;
        while (true) {
            System.out.print("Введите год: ");
            try {
                year = scanner.nextInt();
                if (year < 2000 || year > 2100) {
                    System.out.println("Ошибка: Введите год в диапазоне 2000-2100.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите целое число.");
                scanner.nextLine(); // Очистка буфера
            }
        }

        int month = 0;
        while (true) {
            System.out.print("Введите месяц (1-12): ");
            try {
                month = scanner.nextInt();
                if (month < 1 || month > 12) {
                    System.out.println("Ошибка: Введите месяц в диапазоне 1-12.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите целое число.");
                scanner.nextLine(); // Очистка буфера
            }
        }
        scanner.nextLine();

        try {
            budgetService.setMonthlyBudget(currentUser, currentUser, limit, year, month);
            System.out.println("Бюджет установлен");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Удаляет бюджет по указанному ID.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void deleteBudget() throws SQLException {
        int id = 0;
        while (true) {
            System.out.print("Введите ID бюджета: ");
            try {
                id = scanner.nextInt();
                if (id < 0) {
                    System.out.println("Ошибка: ID не может быть отрицательным.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите целое число.");
                scanner.nextLine(); // Очистка буфера
            }
        }
        scanner.nextLine();

        try {
            budgetService.deleteBudget(id, currentUser);
            System.out.println("Бюджет удален");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Отображает список всех бюджетов текущего пользователя.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void viewBudgets() throws SQLException {
        List<Budget> budgets = budgetService.getUserBudgets(currentUser, currentUser);
        if (budgets.isEmpty()) {
            System.out.println("Нет бюджетов");
        } else {
            budgets.forEach(System.out::println);
        }
    }

    /**
     * Отображает меню управления целями.
     * Позволяет устанавливать, удалять и просматривать цели.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void manageGoals() throws SQLException {
        while (true) {
            System.out.println("1. Установить цель");
            System.out.println("2. Удалить цель");
            System.out.println("3. Просмотреть цели");
            System.out.println("4. Назад");
            int choice = getIntInput("Выберите действие (1-4): ", 1, 4);

            switch (choice) {
                case 1: setGoal(); break;
                case 2: deleteGoal(); break;
                case 3: viewGoals(); break;
                case 4: return;
                default: System.out.println("Неверный выбор");
            }
        }
    }

    /**
     * Устанавливает новую финансовую цель для текущего пользователя.
     * Запрашивает целевую сумму, описание и дату окончания.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void setGoal() throws SQLException {
        double targetAmount = 0;
        while (true) {
            System.out.print("Введите целевую сумму: ");
            try {
                targetAmount = scanner.nextDouble();
                if (targetAmount < 0) {
                    System.out.println("Ошибка: Сумма не может быть отрицательной.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите корректное число (например, 1000.00).");
                scanner.nextLine(); // Очистка буфера
            }
        }
        scanner.nextLine();

        System.out.print("Введите описание: ");
        String description = scanner.nextLine();

        LocalDate endDate = null;
        while (true) {
            System.out.print("Введите дату окончания (yyyy-mm-dd): ");
            String dateInput = scanner.nextLine();
            try {
                endDate = LocalDate.parse(dateInput);
                if (endDate.isBefore(LocalDate.now())) {
                    System.out.println("Ошибка: Дата окончания не может быть в прошлом.");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Некорректный формат даты. Используйте yyyy-mm-dd (например, 2025-03-15).");
            }
        }

        try {
            goalService.setGoal(currentUser, targetAmount, description, endDate);
            System.out.println("Цель установлена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Удаляет цель по-указанному ID.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void deleteGoal() throws SQLException {
        int id = 0;
        while (true) {
            System.out.print("Введите ID цели: ");
            try {
                id = scanner.nextInt();
                if (id < 0) {
                    System.out.println("Ошибка: ID не может быть отрицательным.");
                    continue;
                }
                break;
            } catch (InputMismatchException e) {
                System.out.println("Ошибка: Введите целое число.");
                scanner.nextLine(); // Очистка буфера
            }
        }
        scanner.nextLine();

        try {
            goalService.deleteGoal(id, currentUser);
            System.out.println("Цель удалена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Отображает список всех целей текущего пользователя.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void viewGoals() throws SQLException {
        List<Goal> goals = goalService.getUserGoals(currentUser);
        if (goals.isEmpty()) {
            System.out.println("Нет целей");
        } else {
            goals.forEach(System.out::println);
        }
    }

    /**
     * Отображает меню просмотра статистики.
     * Позволяет просмотреть текущий баланс или сформировать финансовый отчет.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void viewStatistics() throws SQLException {
        while (true) {
            System.out.println("1. Просмотреть текущий баланс");
            System.out.println("2. Сформировать финансовый отчет");
            System.out.println("3. Назад");
            int choice = getIntInput("Выберите действие (1-3): ", 1, 3);

            switch (choice) {
                case 1:
                    double balance = statisticsService.calculateCurrentBalance(currentUser, currentUser);
                    System.out.println("Текущий баланс: " + balance);
                    break;
                case 2:
                    generateFinancialReport();
                    break;
                case 3:
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    /**
     * Формирует и отображает финансовый отчет за указанный период.
     * Запрашивает начальную и конечную даты (опционально).
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void generateFinancialReport() throws SQLException {
        LocalDate startDate = null;
        while (true) {
            System.out.print("Введите начальную дату (yyyy-mm-dd или Enter для полного отчета): ");
            String startInput = scanner.nextLine();
            if (startInput.isEmpty()) {
                startDate = null;
                break;
            }
            try {
                startDate = LocalDate.parse(startInput);
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Некорректный формат даты. Используйте yyyy-mm-dd (например, 2025-03-15).");
            }
        }

        LocalDate endDate = null;
        while (true) {
            System.out.print("Введите конечную дату (yyyy-mm-dd или Enter для полного отчета): ");
            String endInput = scanner.nextLine();
            if (endInput.isEmpty()) {
                endDate = null;
                break;
            }
            try {
                endDate = LocalDate.parse(endInput);
                if (startDate != null && endDate.isBefore(startDate)) {
                    System.out.println("Ошибка: Конечная дата не может быть раньше начальной.");
                    continue;
                }
                break;
            } catch (DateTimeParseException e) {
                System.out.println("Ошибка: Некорректный формат даты. Используйте yyyy-mm-dd (например, 2025-03-15).");
            }
        }

        try {
            String report = statisticsService.generateFinancialReport(currentUser, currentUser, startDate, endDate);
            System.out.println(report);
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Отображает уведомления о превышении бюджета и прогрессе целей.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void getNotifications() throws SQLException {
        List<String> budgetNotifications = notificationService.checkBudgetsAndNotify(currentUser, currentUser);
        List<String> goalNotifications = notificationService.checkGoalsAndNotify(currentUser, currentUser);

        if (budgetNotifications.isEmpty() && goalNotifications.isEmpty()) {
            System.out.println("Нет уведомлений");
        } else {
            System.out.println("Уведомления о бюджетах:");
            budgetNotifications.forEach(System.out::println);
            System.out.println("Уведомления о целях:");
            goalNotifications.forEach(System.out::println);
        }
    }

    /**
     * Отображает меню управления пользователями (доступно только администраторам).
     * Позволяет просматривать, блокировать и удалять пользователей.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void manageUsers() throws SQLException {
        while (true) {
            System.out.println("1. Просмотреть пользователей");
            System.out.println("2. Блокировать пользователя");
            System.out.println("3. Удалить пользователя");
            System.out.println("4. Назад");
            int choice = getIntInput("Выберите действие (1-4): ", 1, 4);

            switch (choice) {
                case 1: viewUsers(); break;
                case 2: blockUser(); break;
                case 3: deleteUser(); break;
                case 4: return;
                default: System.out.println("Неверный выбор");
            }
        }
    }

    /**
     * Отображает список всех пользователей с их транзакциями.
     * Доступно только администраторам.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void viewUsers() throws SQLException {
        List<User> users = userService.getAllUsers(currentUser);
        for (User u : users) {
            System.out.println(u.getEmail() + " - " + u.getUsername() + " - " +
                    (u.isBlocked() ? "Заблокирован" : "Активен"));

            List<Transaction> transactions = transactionService.getUserTransaction(currentUser, u, null, null, null, null);
            if (transactions.isEmpty()) {
                System.out.println("Нет транзакций");
            } else {
                transactions.forEach(System.out::println);
            }
            System.out.println(); // Пустая строка для разделения пользователей
        }
    }

    /**
     * Блокирует или разблокирует пользователя по email.
     * Доступно только администраторам.
     *
     * @throws SQLException если возникает ошибка при работе с базой данных
     */
    private void blockUser() throws SQLException {
        System.out.print("Введите email пользователя: ");
        String email = scanner.nextLine();
        try {
            if (userService.blockUser(currentUser, email)) {
                System.out.println("Пользователь заблокирован");
            } else {
                System.out.println("Пользователь разблокирован");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Удаляет пользователя по email.
     * Доступно только администраторам.
     */
    private void deleteUser() {
        System.out.print("Введите email пользователя: ");
        String email = scanner.nextLine();

        try {
            userService.deleteUser(currentUser, email);
            System.out.println("Пользователь удален");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    /**
     * Запрашивает у пользователя целое число в указанном диапазоне.
     *
     * @param prompt сообщение с запросом ввода
     * @param min    минимальное допустимое значение
     * @param max    максимальное допустимое значение
     * @return введенное пользователем число
     */
    private int getIntInput(String prompt, int min, int max) {
        System.out.print(prompt);
        try {
            int input = scanner.nextInt();
            scanner.nextLine();
            if (input < min || input > max) {
                System.out.println("Неверный выбор");
                return getIntInput(prompt, min, max);
            }
            return input;
        } catch (InputMismatchException e) {
            System.out.println("Ошибка: Требуется целое число!");
            scanner.nextLine();
            return getIntInput(prompt, min, max);
        }
    }
}