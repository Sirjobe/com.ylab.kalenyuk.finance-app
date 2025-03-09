package com.ylab.management;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;

import java.util.Map;
import java.util.Scanner;

public class AdminManager {
    private UserManager userManager; // Для доступа к пользователям
    private TransactionManager transactionManager; // Для Scanner и других методов
    private User currentAdmin; // Текущий администратор

    public AdminManager(UserManager userManager, TransactionManager transactionManager, User currentAdmin) {
        this.userManager = userManager;
        this.transactionManager = transactionManager;
        this.currentAdmin = currentAdmin;
    }

    public void manageAdminFunctions() {
        if (currentAdmin == null || !isAdmin(currentAdmin)) {
            System.out.println("Доступ только для администраторов");
            return;
        }

        Scanner scanner = transactionManager.getScanner();
        boolean managing = true;

        while (managing) {
            System.out.println("Администрирование:");
            System.out.println("1. Просмотреть список пользователей");
            System.out.println("2. Просмотреть транзакции пользователя");
            System.out.println("3. Заблокировать пользователя");
            System.out.println("4. Удалить пользователя");
            System.out.println("0. Назад");

            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Очистка буфера

                switch (choice) {
                    case 1:
                        viewUsers();
                        break;
                    case 2:
                        viewUserTransactions();
                        break;
                    case 3:
                        blockUser();
                        break;
                    case 4:
                        deleteUser();
                        break;
                    case 0:
                        managing = false;
                        break;
                    default:
                        System.out.println("Неверный выбор");
                }
            } catch (Exception e) {
                System.out.println("Введите число");
                scanner.nextLine(); // Очистка буфера при ошибке
            }
        }
    }

    // Проверка, является ли пользователь администратором
    private boolean isAdmin(User user) {
        return user.isAdmin();
    }

    // Просмотр списка пользователей
    private void viewUsers() {
        Map<String, User> users = userManager.getUsers();
        if (users.isEmpty()) {
            System.out.println("Пользователи отсутствуют");
            return;
        }

        System.out.println("Список пользователей:");
        int index = 0;
        for (Map.Entry<String, User> entry : users.entrySet()) {
            String email = entry.getKey();
            User user = entry.getValue();
            System.out.println(index + ". Email: " + email + " | Имя: " + user.getUsername() +
                    " | Заблокирован: " + (user.isBlocked() ? "Да" : "Нет"));
            index++;
        }
    }

    // Просмотр транзакций пользователя
    private void viewUserTransactions() {
        User selectedUser = selectUser("Выберите пользователя для просмотра транзакций:");
        if (selectedUser == null) return;

        java.util.List<Transaction> transactions = selectedUser.getTransactions();
        if (transactions.isEmpty()) {
            System.out.println("У пользователя " + selectedUser.getUsername() + " нет транзакций");
            return;
        }

        System.out.println("Транзакции пользователя " + selectedUser.getUsername() + ":");
        for (Transaction t : transactions) {
            System.out.println(t.getDate() + " | " + t.getType() + " | " + t.getAmount() + " | " + t.getCategory());
        }
    }

    // Блокировка пользователя
    private void blockUser() {
        User selectedUser = selectUser("Выберите пользователя для блокировки:");
        if (selectedUser == null) return;

        if (selectedUser.equals(currentAdmin)) {
            System.out.println("Нельзя заблокировать самого себя");
            return;
        }

        if (selectedUser.isBlocked()) {
            System.out.println("Пользователь " + selectedUser.getUsername() + " уже заблокирован");
        } else {
            selectedUser.setBlocked(true);
            System.out.println("Пользователь " + selectedUser.getUsername() + " заблокирован");
        }
    }

    // Удаление пользователя
    private void deleteUser() {
        User selectedUser = selectUser("Выберите пользователя для удаления:");
        if (selectedUser == null) return;

        if (selectedUser.equals(currentAdmin)) {
            System.out.println("Нельзя удалить самого себя");
            return;
        }

        Scanner scanner = transactionManager.getScanner();
        System.out.println("Подтвердите удаление пользователя " + selectedUser.getUsername() + " (y/n):");
        String accept = scanner.nextLine();
        if (accept.equalsIgnoreCase("y")) {
            userManager.removeUser(selectedUser.getEmail());
            System.out.println("Пользователь " + selectedUser.getUsername() + " удален");
        } else {
            System.out.println("Удаление отменено");
        }
    }

    // Вспомогательный метод для выбора пользователя
    private User selectUser(String prompt) {
        Map<String, User> users = userManager.getUsers();
        if (users.isEmpty()) {
            System.out.println("Пользователи отсутствуют");
            return null;
        }

        viewUsers();
        System.out.println(prompt);
        Scanner scanner = transactionManager.getScanner();

        try {
            int index = scanner.nextInt();
            scanner.nextLine(); // Очистка буфера
            if (index >= 0 && index < users.size()) {
                return users.values().toArray(new User[0])[index];
            } else {
                System.out.println("Неверный индекс");
                return null;
            }
        } catch (Exception e) {
            System.out.println("Введите число");
            scanner.nextLine();
            return null;
        }

    }
}