package com.ylab.repository;

import com.ylab.entity.Transaction;
import com.ylab.entity.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс репозитория для управления финансовыми транзакциями.
 */
public interface TransactionRepository {

    /**
     * Сохраняет новую транзакцию.
     *
     * @param transaction транзакция для сохранения
     */
    void save (Transaction transaction) throws SQLException;

    /**
     * Находит все транзакции пользователя.
     *
     * @param user пользователь, чьи транзакции нужно найти
     * @return список транзакций
     */
    List<Transaction> findByUser (User user) throws SQLException;

    /**
     * Находит транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return транзакция или null, если не найдена
     */
    Transaction findById (int id) throws SQLException;

    /**
     * Удаляет транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции
     */
    void deleteById (int id) throws SQLException;


}
