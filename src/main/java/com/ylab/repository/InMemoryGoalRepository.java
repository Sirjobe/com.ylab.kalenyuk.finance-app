package com.ylab.repository;

import com.ylab.entity.Goal;
import com.ylab.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryGoalRepository implements GoalRepository {
    private List<Goal> goals = new ArrayList<>();
    /**
     * Сохраняет новую цель.
     *
     * @param goal цель для сохранения
     */
    @Override
    public void save(Goal goal) {
        goals.removeIf(g -> g.getId() == goal.getId());
        goals.add(goal);
    }

    /**
     * Находит цели по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return транзакция или null, если не найдена
     */
    @Override
    public Goal findById(int id) {
        return goals.stream()
                .filter(t->t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Находит все цели пользователя.
     *
     * @param user пользователь, чьи цели нужно найти
     * @return список целей
     */
    @Override
    public List<Goal> findByUser(User user) {
        return goals.stream()
                .filter(g->g.getEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Удаляет цель по её идентификатору.
     *
     * @param id идентификатор цели
     */
    @Override
    public void deleteById(int id) {
        goals.removeIf(g->g.getId() == id);
    }

}
