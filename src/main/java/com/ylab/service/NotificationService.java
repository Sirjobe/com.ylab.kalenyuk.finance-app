package com.ylab.service;

import com.ylab.entity.Budget;
import com.ylab.entity.Goal;
import com.ylab.entity.User;

import java.util.List;

/**
 * Сервис для управления уведомлениями о достижении лимитов и целей.
 */
public class NotificationService {
    private final BudgetService budgetService;
    private final GoalService goalService;
    private final EmailSender emailSender;

    public NotificationService(BudgetService budgetService, GoalService goalService, EmailSender emailSender) {
        this.budgetService = budgetService;
        this.goalService = goalService;
        this.emailSender = emailSender;
    }

    /**
     * Проверяет все бюджеты пользователя и отправляет уведомления о превышении.
     *
     * @param user пользователь
     * @return список уведомлений (для API)
     */
    public List<String> checkBudgetsAndNotify(User user, User admin) {
        List<Budget> budgets = budgetService.getUserBudgets(user,admin);
        List<String> notifications = budgets.stream()
                .map(budget -> {
                    String notification = budgetService.checkBudget(budget.getId(), user, admin);
                    if (notification != null) {
                        sendEmailNotification(user, "Превышение бюджета", notification);
                        return notification;
                    }
                    return null;
                })
                .filter(n -> n != null)
                .toList();
        return notifications;
    }

    /**
     * Проверяет все цели пользователя и отправляет уведомления о прогрессе.
     *
     * @param user пользователь
     * @return список уведомлений (для API)
     */
    public List<String> checkGoalsAndNotify(User user, User admin) {
        List<Goal> goals = goalService.getUserGoals(user);
        List<String> notifications = goals.stream()
                .map(goal -> {
                    String progress = goalService.trackGoalProgress(goal.getId(), user, admin);
                    if (progress.contains("Цель достигнута")) {
                        String message = "Поздравляем! " + progress;
                        sendEmailNotification(user, "Достижение цели", message);
                        return message;
                    } else if (progress.contains("Осталось накопить")) {
                        // Можно добавить условие, например, уведомлять, если осталось менее 10%
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
                .toList();
        return notifications;
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
