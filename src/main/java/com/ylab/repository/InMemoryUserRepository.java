package com.ylab.repository;

import com.ylab.entity.User;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class InMemoryUserRepository implements UserRepository {
    private Map<String, User> users = new HashMap<>();
    private final AtomicInteger idGenerator = new AtomicInteger(0);


    /**
     * Сохраняет нового пользователя или обновляет существующего.
     * Если у пользователя нет ID, генерирует новый уникальный ID.
     *
     * @param user пользователь для сохранения
     */
    @Override
    public void save(User user) {
        if (user.getId() == 0) {
            user.setId(idGenerator.incrementAndGet());
        }
        users.put(user.getEmail(), user);
    }

    /**
     * Находит пользователя по его email.
     *
     * @param email адрес электронной почты пользователя
     * @return пользователь или null, если не найден
     */
    @Override
    public User findByEmail(String email) {
        return users.get(email);
    }

    /**
     * Удаляет пользователя по его email.
     *
     * @param email адрес электронной почты пользователя
     */
    @Override
    public void deleteByEmail(String email) {
        users.remove(email);
    }

    /**
     * Возвращает безопасную копию списка всех пользователей.
     *
     * @return список пользователей
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users.values());
    }
}