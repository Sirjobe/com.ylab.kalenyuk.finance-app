package com.ylab.service;

import com.ylab.entity.Budget;
import com.ylab.entity.Goal;
import com.ylab.entity.User;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

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

    public List<String> checkBudgetsAndNotify(User user, User admin) {
        List<String> notifications = new ArrayList<>();
        try {
            List<Budget> budgets = budgetService.getUserBudgets(user, admin);
            if (budgets.isEmpty()) {
                return notifications;
            }

            notifications = budgets.stream()
                    .map(budget -> {
                        String notification = budgetService.checkBudget(budget.getId(), user, admin);
                        if (notification != null) {
                            sendEmailNotification(user, "Превышение бюджета", notification);
                            return notification;
                        }
                        return null;
                    })
                    .filter(n -> n != null)
                    .collect(Collectors.toList());

            if (notifications.isEmpty()) {
                String allFineMessage = "Все бюджеты в пределах лимитов";
                notifications.add(allFineMessage);
            }
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
            notifications.add("Ошибка при проверке бюджета: " + e.getMessage());
        }
        return notifications;
    }

    public void sendImmediateNotification(User user, String subject, String message) {
        sendEmailNotification(user, subject, message);
        System.out.println("Уведомление: " + message);
    }

    public List<String> checkGoalsAndNotify(User user, User admin) {
        try {
            List<Goal> goals = goalService.getUserGoals(user);
            if (goals.isEmpty()) {
                return new ArrayList<>();
            }
            return goals.stream()
                    .map(goal -> {
                        String progress = goalService.trackGoalProgress(goal.getId(), user, admin);
                        if (progress.contains("Цель достигнута")) {
                            String message = "Поздравляем! " + progress;
                            sendEmailNotification(user, "Достижение цели", message);
                            return message;
                        } else if (progress.contains("Осталось накопить")) {
                            double remaining = Double.parseDouble(progress.split("Осталось накопить: ")[1].split("\n")[0]);
                            if (remaining < goal.getTargetAmount() * 0.1) {
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

    public String checkBudgetTransaction(User user, User admin) throws SQLException {
        Budget budget = budgetService.findById(user.getId());
        if (budget != null) {
            return budgetService.checkBudget(budget.getId(), user, admin);
        }
        return null;
    }

    private void sendEmailNotification(User user, String subject, String message) {
        emailSender.sendEmail(user.getEmail(),subject, message);
    }
}