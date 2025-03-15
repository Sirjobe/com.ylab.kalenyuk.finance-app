package com.ylab.repository;

import com.ylab.entity.User;

import java.sql.SQLException;
import java.util.List;

/**
 * Интерфейс репозитория для управления пользователями.
 */
public interface UserRepository {
    /**
     * Сохраняет нового пользователя или обновляет существующего.
     *
     * @param user пользователь для сохранения
     */
    void save (User user);

    /**
     * Находит пользователя по его email.
     *
     * @param email адрес электронной почты пользователя
     * @return пользователь или null, если не найден
     */
    User findByEmail (String email);

    /**
     * Удаляет пользователя по его email.
     *
     * @param email адрес электронной почты пользователя
     */
    void deleteByEmail (String email);

    /**
     * Возвращает список всех пользователей.
     *
     * @return список пользователей
     */
    List<User> findAll () throws SQLException;



}
