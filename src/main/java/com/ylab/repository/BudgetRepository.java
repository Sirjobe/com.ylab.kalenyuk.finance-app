package com.ylab.repository;

import com.ylab.entity.Budget;
import com.ylab.entity.User;

import java.sql.SQLException;
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
    void save (Budget budget) throws SQLException;

    /**
     * Возвращает список бюджета по ID.
     *
     * @param id бюджет для сохранения
     */
    Budget findById(int id) throws SQLException;

    /**
     * Удаляет бюджет по его идентификатору.
     *
     * @param id идентификатор бюджета
     */
    void deleteById (int id) throws SQLException;

    /**
     * Возвращает список конкретного пользователя.
     *
     * @return список пользователей
     */
    List<Budget> findByUser (User user) throws SQLException;

}
