package com.ylab.repository;

import com.ylab.entity.Goal;
import com.ylab.entity.User;

import java.util.List;

/**
 * Интерфейс репозитория для управления финансовыми целями пользователя.
 */
public interface GoalRepository {
    /**
     * Сохраняет новую цель.
     *
     * @param goal цель для сохранения
     */
    void save (Goal goal);

    /**
     * Находит все цели пользователя.
     *
     * @param user пользователь, чьи цели нужно найти
     * @return список целей
     */
    List<Goal> findByUser (User user);

    /**
     * Находит цели по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return транзакция или null, если не найдена
     */
    Goal findById (int id);

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param id идентификатор цели
     */
    void deleteById (int id);


}
