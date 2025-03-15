package com.ylab.service;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;


import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Сервис для предоставления статистики и аналитики по финансам пользователя.
 */
public class StatisticsService {
    private final TransactionService transactionService;

    public StatisticsService(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Подсчитывает текущий баланс пользователя на основе всех транзакций.
     *
     * @param user пользователь, для которого считается баланс
     * @return текущий баланс (доходы минус расходы)
     */
    public double calculateCurrentBalance(User admin, User user) {
        if (!admin.isAdmin() && !admin.getEmail().equals(user.getEmail())) {
            throw new IllegalArgumentException("Вы можете просматривать только свой баланс");
        }
        if (user.isBlocked()) {
            throw new IllegalArgumentException("Нельзя рассчитать баланс для заблокированного пользователя");
        }
        double totalIncome = 0;
        double totalExpense = 0;
        List<Transaction> transaction = transactionService.getUserTransaction(admin, user, null, null, null, null);
        totalIncome = transaction.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
        totalExpense = transaction.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
        return totalIncome - totalExpense;
    }

    /**
     * Подсчитывает суммарный доход и расход за указанный период.
     *
     * @param user      пользователь
     * @param startDate начальная дата периода (null для всех транзакций)
     * @param endDate   конечная дата периода (null для всех транзакций)
     * @return массив [доход, расход]
     */
    public double[] calculateIncomeAndExpense(User admin, User user, LocalDate startDate, LocalDate endDate) {
        double totalIncome = 0;
        double totalExpense = 0;
        List<Transaction> transaction = transactionService.getUserTransaction(admin, user, startDate, endDate, null, null);
        totalIncome = transaction.stream()
                .filter(t -> t.getType() == TransactionType.INCOME)
                .mapToDouble(Transaction::getAmount)
                .sum();
        totalExpense = transaction.stream()
                .filter(t -> t.getType() == TransactionType.EXPENSE)
                .mapToDouble(Transaction::getAmount)
                .sum();
        return new double[]{totalIncome, totalExpense};
    }

    /**
     * Анализирует расходы по категориям за указанный период.
     *
     * @param user      пользователь
     * @param startDate начальная дата периода (null для всех транзакций)
     * @param endDate   конечная дата периода (null для всех транзакций)
     * @return карта с категориями и суммами расходов
     */
    public Map<String, Double> analyzeExpenseByCategory(User admin, User user, LocalDate startDate, LocalDate endDate) {
        List<Transaction> transactions = null;
        transactions = transactionService.getUserTransaction(admin, user, startDate, endDate, null, TransactionType.EXPENSE);
        return transactions.stream().collect(Collectors.groupingBy(
                Transaction::getCategory,
                Collectors.summingDouble(Transaction::getAmount)));
    }

    /**
     * Формирует отчет о финансовом состоянии пользователя за период.
     *
     * @param user      пользователь
     * @param startDate начальная дата периода (null для всех транзакций)
     * @param endDate   конечная дата периода (null для всех транзакций)
     * @return строка с отчетом
     */
    public String generateFinancialReport(User admin, User user, LocalDate startDate, LocalDate endDate) {
        double balance = calculateCurrentBalance(admin, user);
        double[] incomeAndExpense = calculateIncomeAndExpense(admin, user, startDate, endDate);
        Map<String, Double> expenseByCategory = analyzeExpenseByCategory(admin, user, startDate, endDate);

        StringBuilder report = new StringBuilder();
        report.append("Финансовый отчет для ").append(user.getUsername());
        if (startDate != null && endDate != null) {
            report.append(" за период ").append(startDate).append(" по ").append(endDate);
        }
        report.append(":\n");
        report.append("Текущий баланс: ").append(String.format(Locale.US, "%.2f", balance)).append("\n");
        report.append("Доходы: ").append(String.format(Locale.US, "%.2f", incomeAndExpense[0])).append("\n");
        report.append("Расходы: ").append(String.format(Locale.US, "%.2f", incomeAndExpense[1])).append("\n");
        report.append("Расходы по категориям:\n");
        if (expenseByCategory.isEmpty()) {
            report.append(" Нет расходов за этот период\n");
        } else {
            expenseByCategory.forEach((category, amount) -> {
                report.append("  ").append(category).append(": ").append(String.format(Locale.US, "%.2f", amount)).append("\n");
            });
        }
        return report.toString();
    }
}
