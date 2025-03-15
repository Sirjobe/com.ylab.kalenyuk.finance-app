package com.ylab;

import com.ylab.entity.*;
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
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

public class App {
    /**
     *  Репозитории (in-memory реализации)
     */
    private final UserRepository userRepository;
    private final TransactionRepository transactionRepository;
    private final BudgetRepository budgetRepository;
    private final GoalRepository goalRepository;

    /**
     *  Сервисы
     */
    private final UserService userService;
    private final TransactionService transactionService;
    private final BudgetService budgetService;
    private final StatisticsService statisticsService;
    private final GoalService goalService;
    private final EmailSender emailSender;
    private final NotificationService notificationService;

    /**
     *  Инструменты ввода вывода
     */
    private static final Scanner scanner = new Scanner(System.in);
    private static User currentUser = null;

    public App(UserRepository userRepository, TransactionRepository transactionRepository,
               BudgetRepository budgetRepository, GoalRepository goalRepository) {
        this.userRepository = userRepository;
        this.transactionRepository = transactionRepository;
        this.budgetRepository = budgetRepository;
        this.goalRepository = goalRepository;

        this.userService = new UserService(userRepository);
        this.transactionService = new TransactionService(transactionRepository);
        this.budgetService = new BudgetService(budgetRepository, transactionService);
        this.statisticsService = new StatisticsService(transactionService);
        this.goalService = new GoalService(goalRepository, statisticsService);
        this.emailSender = new ConsoleEmailSender();
        this.notificationService = new NotificationService(budgetService, goalService, emailSender,transactionService);
    }

    public static void main(String[] args) throws Exception {
       Properties props = new Properties();
       try (FileInputStream fis = new FileInputStream("src/main/resources/application.properties")){
           props.load(fis);
       }

        String changelogPath = props.getProperty("liquibase.changelog").replace("classpath:", "");
        System.out.println("Путь к changelog: " + changelogPath);
        java.net.URL resource = App.class.getClassLoader().getResource("db/migration/changelog-master.xml");
        System.out.println("changelog-master.xml в classpath: " + (resource != null ? resource : "не найден"));

        boolean connected = false;
        int retries = 5;
        while (!connected && retries > 0) {
            try (java.sql.Connection conn = java.sql.DriverManager.getConnection(
                    props.getProperty("db.url"),
                    props.getProperty("db.username"),
                    props.getProperty("db.password"))) {
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

       UserRepository userRepository = new JdbcUserRepository(props);
       TransactionRepository transactionRepository = new JdbcTransactionRepository(props);
       BudgetRepository budgetRepository = new JdbcBudgetRepository(props);
       GoalRepository goalRepository = new JdbcGoalRepository(props);

       App app = new App(userRepository, transactionRepository, budgetRepository, goalRepository);
       app.run();
    }

    private void run() throws SQLException{
        while (true) {
            if (currentUser == null) {
                showGuestMenu();
            } else {
                showUserMenu();
            }
        }
    }


    private void showGuestMenu() {
        System.out.println("1. Регистрация");
        System.out.println("2. Вход");
        System.out.println("3. Выход");
        int choice = getIntInput("Выберите действие (1-3): ", 1, 3);

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


    private void manageTransactions() throws SQLException {
        while (true) {
            System.out.println("1. Добавить транзакцию");
            System.out.println("2. Редактировать транзакцию");
            System.out.println("3. Удалить транзакцию");
            System.out.println("4. Просмотреть транзакции");
            System.out.println("5. Назад");
            int choice = getIntInput("Выберите действие (1-5): ", 1, 5);

            switch (choice) {
                case 1: addTransaction(); break;
                case 2: editTransaction(); break;
                case 3: deleteTransaction(); break;
                case 4: viewTransactions(); break;
                case 5: return;
                default: System.out.println("Неверный выбор");
            }
        }
    }

    private void addTransaction() throws SQLException{
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

    private void editTransaction() throws SQLException {
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

    private void deleteTransaction()  throws SQLException{
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

    private void viewTransactions() throws SQLException {
        List<Transaction> transactions = transactionService.getUserTransaction(currentUser, currentUser, null, null, null, null);
        if (transactions.isEmpty()) {
            System.out.println("Нет транзакций");
        } else {
            transactions.forEach(System.out::println);
        }
    }


    private void manageBudgets() throws SQLException {
        while (true) {
            System.out.println("1. Установить бюджет");
            System.out.println("2. Удалить бюджет");
            System.out.println("3. Просмотреть бюджеты");
            System.out.println("4. Назад");
            int choice = getIntInput("Выберите действие (1-7): ", 1, 4);

            switch (choice) {
                case 1: setBudget(); break;
                case 2: deleteBudget(); break;
                case 3: viewBudgets(); break;
                case 4: return;
                default: System.out.println("Неверный выбор");
            }
        }
    }

    private void setBudget() throws SQLException {
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

    private void deleteBudget() throws SQLException {
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

    private void viewBudgets() throws SQLException {
        List<Budget> budgets = budgetService.getUserBudgets(currentUser, currentUser);
        if (budgets.isEmpty()) {
            System.out.println("Нет бюджетов");
        } else {
            budgets.forEach(System.out::println);
        }
    }


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

    private void setGoal() throws SQLException{
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

    private void deleteGoal() throws SQLException {
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

    private void viewGoals() throws SQLException {
        List<Goal> goals = goalService.getUserGoals(currentUser);
        if (goals.isEmpty()) {
            System.out.println("Нет целей");
        } else {
            goals.forEach(System.out::println);
        }
    }


    private void viewStatistics() throws SQLException {
        double balance = statisticsService.calculateCurrentBalance(currentUser, currentUser);
        System.out.println("Текущий баланс: " + balance);
    }


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

    private void blockUser() throws SQLException {
        System.out.print("Введите email пользователя: ");
        String email = scanner.nextLine();
        try {
            if(userService.blockUser(currentUser, email)){
                System.out.println("Пользователь заблокирован");
            }else{
                System.out.println("Пользователь разблокирован");
            }
        } catch (IllegalArgumentException e) {
            System.out.println("Ошибка: " + e.getMessage());
        }
    }

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

    private int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                String input = scanner.nextLine();
                int value = Integer.parseInt(input);

                if (value >= min && value <= max) {
                    return value;
                }

                System.out.printf("Введите число от %d до %d!%n", min, max);
            } catch (NumberFormatException e) {
                System.out.println("Ошибка: Требуется целое число!");
            }
        }
    }
}