package com.ylab.repository;

import com.ylab.entity.Budget;
import com.ylab.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryBudgetRepository implements BudgetRepository {
    private List<Budget> budgets = new ArrayList<>();
    /**
     * Сохраняет новый бюджет.
     *
     * @param budget бюджет для сохранения
     */
    @Override
    public void save(Budget budget) {
        budgets.removeIf(b -> b.getId() == budget.getId());
        budgets.add(budget);
    }

    /**
     * Удаляет бюджет по его идентификатору.
     *
     * @param id идентификатор бюджета
     */
    @Override
    public void deleteById(int id) {
        budgets.removeIf(b->b.getId() == id);
    }

    /**
     * Возвращает список бюджета по ID.
     *
     * @return список бюджета
     */
    @Override
    public Budget findById(int id) {
        return budgets.stream()
                .filter(b -> b.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Возвращает список конкретного пользователя.
     *
     * @return список пользователей
     */
    @Override
    public List<Budget> findByUser(User user) {
        return budgets.stream().filter(b->b.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
    }
}