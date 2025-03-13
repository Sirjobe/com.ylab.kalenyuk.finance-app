package com.ylab;

import com.ylab.entity.*;
import com.ylab.repository.*;
import com.ylab.service.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class App {
    /**
     *  Репозитории (in-memory реализации)
     */
    private static final UserRepository userRepository = new InMemoryUserRepository();
    private static final TransactionRepository transactionRepository = new InMemoryTransactionRepository();
    private static final BudgetRepository budgetRepository = new InMemoryBudgetRepository();
    private static final GoalRepository goalRepository = new InMemoryGoalRepository();

    /**
     *  Сервисы
     */
    private static final UserService userService = new UserService(userRepository);
    private static final TransactionService transactionService = new TransactionService(transactionRepository);
    private static final BudgetService budgetService = new BudgetService(budgetRepository, transactionService);
    private static final StatisticsService statisticsService = new StatisticsService(transactionService);
    private static final GoalService goalService = new GoalService(goalRepository, statisticsService);
    private static final EmailSender emailSender = new ConsoleEmailSender();
    private static final NotificationService notificationService = new NotificationService(budgetService, goalService, emailSender);

    /**
     *  Инструменты ввода вывода
     */
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public static void main(String[] args) {
        while (true) {
            if (currentUser == null) {
                showGuestMenu();
            } else {
                showUserMenu();
            }
        }
    }


    private static void showGuestMenu() {
        System.out.println("1. Регистрация");
        System.out.println("2. Вход");
        System.out.println("3. Выход");
        System.out.print("Выберите действие: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера

        switch (choice) {
            case 1:
                register();
                break;
            case 2:
                login();
                break;
            case 3:
                System.exit(0);
                break;
            default:
                System.out.println("Неверный выбор");
        }
    }


    private static void register() {
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


    private static void login() {
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


    private static void showUserMenu() {
        System.out.println("1. Управление профилем");
        System.out.println("2. Управление транзакциями");
        System.out.println("3. Управление бюджетом");
        System.out.println("4. Управление целями");
        System.out.println("5. Просмотр статистики");
        System.out.println("6. Получить уведомления");
        if (currentUser.isAdmin()) {
            System.out.println("7. Управление пользователями");
            System.out.println("8. Выход");
        } else {
            System.out.println("7. Выход");
        }
        System.out.print("Выберите действие: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                manageProfile();
                break;
            case 2:
                manageTransactions();
                break;
            case 3:
                manageBudgets();
                break;
            case 4:
                manageGoals();
                break;
            case 5:
                viewStatistics();
                break;
            case 6:
                getNotifications();
                break;
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
            default:
                System.out.println("Неверный выбор");
        }
    }


    private static void manageProfile() {
        System.out.println("1. Редактировать профиль");
        System.out.println("2. Удалить аккаунт");
        System.out.println("3. Назад");
        System.out.print("Выберите действие: ");
        int choice = scanner.nextInt();
        scanner.nextLine();

        switch (choice) {
            case 1:
                editProfile();
                break;
            case 2:
                deleteAccount();
                break;
            case 3:
                return;
            default:
                System.out.println("Неверный выбор");
        }
    }

    private static void editProfile() {
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

    private static void deleteAccount() {
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


    private static void manageTransactions() {
        while (true) {
            System.out.println("1. Добавить транзакцию");
            System.out.println("2. Редактировать транзакцию");
            System.out.println("3. Удалить транзакцию");
            System.out.println("4. Просмотреть транзакции");
            System.out.println("5. Назад");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    addTransaction();
                    break;
                case 2:
                    editTransaction();
                    break;
                case 3:
                    deleteTransaction();
                    break;
                case 4:
                    viewTransactions();
                    break;
                case 5:
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private static void addTransaction() {
        System.out.print("Введите сумму: ");
        double amount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Введите описание: ");
        String description = scanner.nextLine();
        System.out.print("Введите категорию: ");
        String category = scanner.nextLine();
        System.out.print("Введите дату (yyyy-mm-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        System.out.print("Введите тип (INCOME/EXPENSE): ");
        TransactionType type = TransactionType.valueOf(scanner.nextLine().toUpperCase());

        try {
            transactionService.createTransaction(amount, description, category, date, type, currentUser.getEmail());
            System.out.println("Транзакция добавлена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void editTransaction() {
        System.out.print("Введите ID транзакции: ");
        int id = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Введите новую сумму (или 0, чтобы не менять): ");
        double newAmount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Введите новое описание (или Enter, чтобы не менять): ");
        String newDescription = scanner.nextLine();
        System.out.print("Введите новую категорию (или Enter, чтобы не менять): ");
        String newCategory = scanner.nextLine();
        System.out.print("Введите новую дату (yyyy-mm-dd или Enter, чтобы не менять): ");
        String dateInput = scanner.nextLine();
        LocalDate newDate = dateInput.isEmpty() ? null : LocalDate.parse(dateInput);
        System.out.print("Введите новый тип (INCOME/EXPENSE или Enter, чтобы не менять): ");
        String typeInput = scanner.nextLine();
        TransactionType newType = typeInput.isEmpty() ? null : TransactionType.valueOf(typeInput.toUpperCase());

        try {
            transactionService.editTransaction(id, currentUser, newAmount, newDescription, newCategory, newDate, newType);
            System.out.println("Транзакция обновлена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteTransaction() {
        System.out.print("Введите ID транзакции: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try {
            transactionService.deleteTransaction(id, currentUser);
            System.out.println("Транзакция удалена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void viewTransactions() {
        List<Transaction> transactions = transactionService.getUserTransaction(currentUser, currentUser, null, null, null, null);
        if (transactions.isEmpty()) {
            System.out.println("Нет транзакций");
        } else {
            transactions.forEach(System.out::println);
        }
    }


    private static void manageBudgets() {
        while (true) {
            System.out.println("1. Установить бюджет");
            System.out.println("2. Удалить бюджет");
            System.out.println("3. Просмотреть бюджеты");
            System.out.println("4. Назад");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    setBudget();
                    break;
                case 2:
                    deleteBudget();
                    break;
                case 3:
                    viewBudgets();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private static void setBudget() {
        System.out.print("Введите лимит: ");
        double limit = scanner.nextDouble();
        System.out.print("Введите год: ");
        int year = scanner.nextInt();
        System.out.print("Введите месяц (1-12): ");
        int month = scanner.nextInt();
        scanner.nextLine();

        try {
            budgetService.setMonthlyBudget(currentUser, currentUser, limit, year, month);
            System.out.println("Бюджет установлен");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteBudget() {
        System.out.print("Введите ID бюджета: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try {
            budgetService.deleteBudget(id, currentUser);
            System.out.println("Бюджет удален");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void viewBudgets() {
        List<Budget> budgets = budgetService.getUserBudgets(currentUser, currentUser);
        if (budgets.isEmpty()) {
            System.out.println("Нет бюджетов");
        } else {
            budgets.forEach(System.out::println);
        }
    }


    private static void manageGoals() {
        while (true) {
            System.out.println("1. Установить цель");
            System.out.println("2. Удалить цель");
            System.out.println("3. Просмотреть цели");
            System.out.println("4. Назад");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    setGoal();
                    break;
                case 2:
                    deleteGoal();
                    break;
                case 3:
                    viewGoals();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private static void setGoal() {
        System.out.print("Введите целевую сумму: ");
        double targetAmount = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Введите описание: ");
        String description = scanner.nextLine();
        System.out.print("Введите дату окончания (yyyy-mm-dd): ");
        LocalDate endDate = LocalDate.parse(scanner.nextLine());

        try {
            goalService.setGoal(currentUser, targetAmount, description, endDate);
            System.out.println("Цель установлена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteGoal() {
        System.out.print("Введите ID цели: ");
        int id = scanner.nextInt();
        scanner.nextLine();

        try {
            goalService.deleteGoal(id, currentUser);
            System.out.println("Цель удалена");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void viewGoals() {
        List<Goal> goals = goalService.getUserGoals(currentUser);
        if (goals.isEmpty()) {
            System.out.println("Нет целей");
        } else {
            goals.forEach(System.out::println);
        }
    }


    private static void viewStatistics() {
        double balance = statisticsService.calculateCurrentBalance(currentUser, currentUser);
        System.out.println("Текущий баланс: " + balance);
    }


    private static void getNotifications() {
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

    private static void manageUsers() {
        while (true) {
            System.out.println("1. Просмотреть пользователей");
            System.out.println("2. Блокировать пользователя");
            System.out.println("3. Удалить пользователя");
            System.out.println("4. Назад");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    viewUsers();
                    break;
                case 2:
                    blockUser();
                    break;
                case 3:
                    deleteUser();
                    break;
                case 4:
                    return;
                default:
                    System.out.println("Неверный выбор");
            }
        }
    }

    private static void viewUsers() {
        List<User> users = userService.getAllUsers(currentUser);
        users.forEach(u -> System.out.println(u.getEmail() + " - " + u.getUsername() + " - " + (u.isBlocked() ? "Заблокирован" : "Активен")));
        
    }

    private static void blockUser() {
        System.out.print("Введите email пользователя: ");
        String email = scanner.nextLine();

        try {
            userService.blockUser(currentUser, email);
            System.out.println("Пользователь заблокирован");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

    private static void deleteUser() {
        System.out.print("Введите email пользователя: ");
        String email = scanner.nextLine();

        try {
            userService.deleteUser(currentUser, email);
            System.out.println("Пользователь удален");
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }
}