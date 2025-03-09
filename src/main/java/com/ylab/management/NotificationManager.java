package com.ylab.management;

import com.ylab.entity.Budget;
import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;


import java.time.LocalDate;
import java.io.FileWriter;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

public class NotificationManager {
    private User currentUser;
    private TransactionManager transactionManager;
    private final String logFile = "notifications.log"; // Файл для записи уведомлений

    public NotificationManager(User currentUser, TransactionManager transactionManager) {
        this.currentUser = currentUser;
        this.transactionManager = transactionManager;
    }

    // Проверка лимитов и отправка уведомлений
    public String checkAndNotify() {
        if (currentUser == null) {
            return "{\"status\": \"error\", \"message\": \"Пользователь не авторизован\"}";
        }

        StringBuilder notifications = new StringBuilder();
        boolean hasNotifications = false;

        // Проверка бюджетов
        for (Budget budget : currentUser.getBudgets()) {
            double totalExpenses = 0;
            for (Transaction t : currentUser.getTransactions()) {
                LocalDate date = t.getDate();
                if (!date.isBefore(budget.getStart()) && !date.isAfter(budget.getEnd()) &&
                        t.getType() == TransactionType.EXPENSE) {
                    totalExpenses += t.getAmount();
                }
            }

            double remaining = budget.getLimit() - totalExpenses;
            String message = null;

            // Уведомление при превышении или близости к лимиту (10%)
            if (remaining <= 0) {
                message = "Бюджет " + budget.getLimit() + " превышен. Остаток: " + String.format("%.2f", remaining);
            } else if (remaining < budget.getLimit() * 0.1) {
                message = "Бюджет " + budget.getLimit() + "接近 к лимиту. Остаток: " + String.format("%.2f", remaining);
            }

            if (message != null) {
                hasNotifications = true;
                notifications.append(message).append("\n");
                logNotification(message); // Запись в файл
            }
        }

        // JSON-ответ
        if (hasNotifications) {
            return "{\"status\": \"sent\", \"message\": \"" + notifications.toString().trim().replace("\n", "; ") + "\"}";
        } else {
            return "{\"status\": \"none\", \"message\": \"Лимиты в порядке\"}";
        }
    }

    public void logNotification(String message) {
        try (FileWriter writer = new FileWriter(logFile, true)) {
            String timestamp = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            writer.write(timestamp + " | " + message + "\n");
            System.out.println("Имитация отправки email: " + message);
        } catch (IOException e) {
            System.err.println("Ошибка записи уведомления: " + e.getMessage());
        }
    }

    // Для вызова через меню
    public void manageNotifications() {
        if (currentUser == null) {
            System.out.println("Пользователь не авторизован");
            return;
        }
        System.out.println("Результат проверки лимитов:");
        String result = checkAndNotify();
        System.out.println("API-ответ: " + result);
    }
}