package com.ylab.repository;

import com.ylab.entity.Budget;
import com.ylab.entity.User;

import java.util.List;

/**
 * Интерфейс репозитория для управления бюджетами пользователя.
 */
public interface BudgetRepository {

    /**
     * Сохраняет новый бюджет.
     *
     * @param budget бюджет для сохранения
     */
    void save (Budget budget);

    /**
     * Возвращает список бюджета по ID.
     *
     * @param id бюджет для сохранения
     */
    Budget findById(int id);

    /**
     * Удаляет бюджет по его идентификатору.
     *
     * @param id идентификатор бюджета
     */
    void deleteById (int id);

    /**
     * Возвращает список конкретного пользователя.
     *
     * @return список пользователей
     */
    List<Budget> findByUser (User user);

}
