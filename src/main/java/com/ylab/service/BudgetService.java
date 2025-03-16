package com.ylab.service;

import com.ylab.entity.Budget;
import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;
import com.ylab.repository.BudgetRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

/**
 * Сервис для управления бизнес-логикой бюджета.
 */
public class BudgetService {
    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;

    public BudgetService(BudgetRepository budgetRepository, TransactionService transactionService) {
        this.budgetRepository = budgetRepository;
        this.transactionService = transactionService;
    }

    /**
     * Устанавливает месячный бюджет для пользователя.
     *
     * @param admin  администратор
     * @param targetUser  пользователь, для которого устанавливается бюджет
     * @param limit лимит бюджета на месяц
     * @param year  год бюджета
     * @param month месяц бюджета (1-12)
     * @throws IllegalArgumentException если данные некорректны
     */
    public void setMonthlyBudget(User admin, User targetUser, double limit, int year, int month) {
        if (!admin.isAdmin() && !admin.getEmail().equals(targetUser.getEmail())) {
            throw new IllegalArgumentException("Вы можете устанавливать бюджет только для себя");
        }
        if (limit <= 0) {
            throw new IllegalArgumentException("Лимит бюджета должен быть положительным");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Месяц должен быть от 1 до 12");
        }
        if (year < 1) {
            throw new IllegalArgumentException("Год должен быть положительным");
        }

        YearMonth yearMonth = YearMonth.of(year, month);
        LocalDate startDate = yearMonth.atDay(1);
        LocalDate endDate = yearMonth.atEndOfMonth();

        Budget budget = new Budget(limit, startDate, endDate, targetUser.getEmail());
        try {
            budgetRepository.save(budget);
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }

    }

    /**
     * Удаляет бюджет по его идентификатору.
     *
     * @param budgetId    идентификатор бюджета
     * @param currentUser текущий пользователь (для проверки прав)
     * @throws IllegalArgumentException если бюджет не найден или нет прав
     */
    public void deleteBudget(int budgetId, User currentUser)  {
        try {
        Budget budget = budgetRepository.findById(budgetId);
        if (budget == null) {
            throw new IllegalArgumentException("Бюджет не найден");
        }
        if (!budget.getEmail().equals(currentUser.getEmail()) && !currentUser.isAdmin()) {
            throw new IllegalArgumentException("Вы можете удалять только свои бюджеты");
        }

            budgetRepository.deleteById(budgetId);
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }

    }

    /**
     * Возвращает список всех бюджетов пользователя.
     *
     * @param targetUser пользователь, чьи бюджеты нужно получить
     * @return список бюджетов
     */
    public List<Budget> getUserBudgets(User admin, User targetUser) throws SQLException {
        if (!admin.isAdmin() && !admin.getEmail().equals(targetUser.getEmail())) {
            throw new IllegalArgumentException("Вы можете просматривать только свои бюджеты");
        }
        return budgetRepository.findByUser(targetUser);
    }

    /**
     * Проверяет превышение бюджета и возвращает уведомление, если лимит превышен.
     *
     * @param budgetId идентификатор бюджета для проверки
     * @param targetUser     пользователь, чей бюджет проверяется
     * @return сообщение о состоянии бюджета или null, если всё в порядке
     */
    public String checkBudget(int budgetId, User targetUser, User admin) {
        try {
            Budget budget = budgetRepository.findById(budgetId);
            if (budget == null) {
                return "Бюджет не найден";
            }
            if (!budget.getEmail().equals(targetUser.getEmail()) && !admin.isAdmin()) {
                throw new IllegalArgumentException("Вы можете проверять только свои бюджеты");
            }

            List<Transaction> transactions = transactionService.getTransactionsByUserAndPeriod(
                    targetUser,
                    budget.getStart(),
                    budget.getEnd()
            );
            double totalExpenses = transactions.stream()
                    .filter(t -> t.getType() == TransactionType.EXPENSE) // Исправлено
                    .mapToDouble(Transaction::getAmount)
                    .sum();

            if (totalExpenses > budget.getLimit()) {
                double overrun = totalExpenses - budget.getLimit();
                return String.format("Бюджет превышен! Расходы: %.2f, Лимит: %.2f, Превышение: %.2f",
                        totalExpenses, budget.getLimit(), overrun);
            } else {
                return String.format("Бюджет в пределах нормы: потрачено %.2f из %.2f",
                        totalExpenses, budget.getLimit());
            }
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
            return "Ошибка при проверке бюджета: " + e.getMessage();
        }
    }

    public Budget findById(int budgetId) throws SQLException {
        return budgetRepository.findById(budgetId);
    }



}
