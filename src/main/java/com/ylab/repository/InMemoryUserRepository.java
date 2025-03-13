package com.ylab.repository;

import com.ylab.entity.User;

import java.util.*;

public class InMemoryUserRepository implements UserRepository   {
    private List<User> users = new ArrayList<>();

       public void test(){
           users.add(new User("kalenyuk@mail.ru","Sergey","12345678", false));
        }

    /**
     * Сохраняет нового пользователя или обновляет существующего.
     *
     * @param user пользователь для сохранения
     */
    @Override
    public void save(User user) {
        users.removeIf(u -> u.getEmail().equals(user.getEmail()));
        users.add(user);
    }

    /**
     * Находит пользователя по его email.
     *
     * @param email адрес электронной почты пользователя
     * @return пользователь или null, если не найден
     */
    @Override
    public User findByEmail(String email) {
        return users.stream()
                .filter(u -> u.getEmail().equals(email))
                .filter(u->!u.isBlocked())
                .findFirst()
                .orElse(null);
    }

    /**
     * Удаляет пользователя по его email.
     *
     * @param email адрес электронной почты пользователя
     */
    @Override
    public void deleteByEmail(String email) {
        users.removeIf(u -> u.getEmail().equals(email));
    }

    /**
     * Возвращает безопасную копию списка всех пользователей.
     *
     * @return список пользователей
     */
    @Override
    public List<User> findAll() {
        return new ArrayList<>(users);
    }
}
