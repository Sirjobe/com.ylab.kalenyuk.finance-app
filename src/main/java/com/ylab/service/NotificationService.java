package com.ylab.service;

import com.ylab.entity.Budget;
import com.ylab.entity.Goal;
import com.ylab.entity.User;

import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления уведомлениями о достижении лимитов и целей.
 */
public class NotificationService {
    private final BudgetService budgetService;
    private final GoalService goalService;
    private final EmailSender emailSender;
    private final TransactionService transactionService;

    public NotificationService(BudgetService budgetService, GoalService goalService, EmailSender emailSender, TransactionService transactionService) {
        this.budgetService = budgetService;
        this.goalService = goalService;
        this.emailSender = emailSender;
        this.transactionService = transactionService;
    }

    /**
     * Проверяет все бюджеты пользователя и отправляет уведомления о превышении.
     *
     * @param user пользователь
     * @return список уведомлений (для API)
     */
    public List<String> checkBudgetsAndNotify(User user, User admin) {
        List<String> notifications = null;
        try {
            List<Budget> budgets = budgetService.getUserBudgets(user, admin);
            notifications = budgets.stream()
                    .map(budget -> {
                        String notification = null;
                        notification = budgetService.checkBudget(budget.getId(), user, admin);
                        if (notification != null) {
                            sendEmailNotification(user, "Превышение бюджета", notification);
                            return notification;
                        }
                        return null;
                    })
                    .filter(n -> n != null)
                    .toList();
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return notifications;
    }

    /**
     * Проверяет все цели пользователя и отправляет уведомления о прогрессе.
     *
     * @param user пользователь
     * @return список уведомлений (для API)
     */
    public List<String> checkGoalsAndNotify(User user, User admin) {
        try {
            List<Goal> goals = goalService.getUserGoals(user);
            return goals.stream()
                    .map(goal -> {
                        String progress = null;
                        progress = goalService.trackGoalProgress(goal.getId(), user, admin);
                        if (progress.contains("Цель достигнута")) {
                            String message = "Поздравляем! " + progress;
                            sendEmailNotification(user, "Достижение цели", message);
                            return message;
                        } else if (progress.contains("Осталось накопить")) {
                            double remaining = Double.parseDouble(progress.split("Осталось накопить: ")[1].split("\n")[0]);
                            if (remaining < goal.getTargetAmount() * 0.1) { // Осталось менее 10%
                                String message = "Вы близки к цели!\n" + progress;
                                sendEmailNotification(user, "Прогресс по цели", message);
                                return message;
                            }
                        }
                        return null;
                    })
                    .filter(n -> n != null)
                    .collect(Collectors.toList());
        } catch (SQLException e) {
            System.err.println("Ошибка при проверке бюджета: " + e.getMessage());
            return List.of();
        }
    }

    /**
     * Проверяет бюджет с учетом транзакций и возвращает уведомление.
     * Оставлен как публичный метод для гибкости, но делегирует логику в BudgetService.
     *
     * @param user пользователь
     * @param admin администратор
     * @return уведомление или null
     */
    public String checkBudgetTransaction(User user, User admin) throws SQLException {
        int budgetId = budgetService.findById(user.getId()).getId();
        return budgetService.checkBudget(budgetId, user, admin);
    }

    /**
     * Отправляет email-уведомление пользователю.
     *
     * @param user    пользователь
     * @param subject тема письма
     * @param message текст уведомления
     */
    private void sendEmailNotification(User user, String subject, String message) {
        emailSender.sendEmail(user.getEmail(), subject, message);
    }
}
