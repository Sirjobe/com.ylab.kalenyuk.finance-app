package com.ylab.service;

import com.ylab.entity.Goal;
import java.util.Locale;
import com.ylab.entity.User;
import com.ylab.repository.GoalRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Сервис для управления финансовыми целями пользователя.
 */
public class GoalService {
    private final GoalRepository goalRepository;
    private final StatisticsService statisticsService;

    public GoalService(GoalRepository goalRepository, StatisticsService statisticsService ) {
        this.goalRepository = goalRepository;
        this.statisticsService = statisticsService;
    }

    /**
     * Устанавливает новую финансовую цель для пользователя.
     *
     * @param user      пользователь, для которого устанавливается цель
     * @param targetAmount целевая сумма накоплений
     * @param description описание цели
     * @param endDate     дата окончания цели
     * @throws IllegalArgumentException если данные некорректны
     */
    public void setGoal(User user, double targetAmount, String description, LocalDate endDate) throws SQLException {
        if (targetAmount <= 0) {
            throw new IllegalArgumentException("Целевая сумма должна быть положительной");
        }
        if (description == null || description.trim().isEmpty()) {
            throw new IllegalArgumentException("Описание цели не может быть пустым");
        }
        if (endDate == null) {
            throw new IllegalArgumentException("Дата окончания цели обязательна");
        }
        if (endDate.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("Дата окончания не может быть в прошлом");
        }

        Goal goal = new Goal(targetAmount, LocalDate.now(), endDate , description , user.getEmail());
        goalRepository.save(goal);
    }

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param goalId      идентификатор цели
     * @param currentUser текущий пользователь (для проверки прав)
     * @throws IllegalArgumentException если цель не найдена или нет прав
     */
    public void deleteGoal(int goalId, User currentUser)  {
        try {
            Goal goal = goalRepository.findById(goalId);
            if(goal == null) {
                throw new IllegalArgumentException("Цель не найдена");
            }
            if(!goal.getEmail().equals(currentUser.getEmail()) && !currentUser.isAdmin()) {
                throw new IllegalArgumentException("Вы можете удалять только свои цели");
            }
            goalRepository.deleteById(goalId);
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    /**
     * Возвращает список всех целей пользователя.
     *
     * @param user пользователь
     * @return список целей
     */
    public List<Goal> getUserGoals(User user) throws SQLException {
        return goalRepository.findByUser(user);
    }

    /**
     * Отслеживает прогресс по цели.
     *
     * @param goalId      идентификатор цели
     * @param currentUser текущий пользователь
     * @return строка с информацией о прогрессе
     * @throws IllegalArgumentException если цель не найдена или нет прав
     */
    public String trackGoalProgress(int goalId, User currentUser, User admin) {
        StringBuilder progressReport = null;
        try {
            Goal goal = goalRepository.findById(goalId);
            if (goal == null) {
                throw new IllegalArgumentException("Цель не найдена");
            }
            if (!goal.getEmail().equals(currentUser.getEmail()) && !currentUser.isAdmin()) {
                throw new IllegalArgumentException("Вы можете отслеживать только свои цели");
            }

            double currentBalance = statisticsService.calculateCurrentBalance(admin, currentUser);
            double progress = Math.min(currentBalance, goal.getTargetAmount());
            double progressPercentage = (progress / goal.getTargetAmount()) * 100;

            progressReport = new StringBuilder();
            progressReport.append("Прогресс по цели (ID: ").append(goalId).append("):\n");
            progressReport.append("Целевая сумма: ").append(String.format(Locale.US, "%.2f", goal.getTargetAmount())).append("\n");
            progressReport.append("Текущий баланс: ").append(String.format(Locale.US, "%.2f", currentBalance)).append("\n");
            progressReport.append("Достигнуто: ").append(String.format(Locale.US, "%.2f", progress)).append("\n");
            progressReport.append("Прогресс: ").append(String.format(Locale.US, "%.2f%%", progressPercentage)).append("\n");
            if (progress >= goal.getTargetAmount()) {
                progressReport.append("Цель достигнута!\n");
            } else {
                double remaining = goal.getTargetAmount() - progress;
                progressReport.append("Осталось накопить: ").append(String.format(Locale.US, "%.2f", remaining)).append("\n");
            }
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return progressReport.toString();

    }

}
