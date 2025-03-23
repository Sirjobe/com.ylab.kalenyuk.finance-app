package com.ylab.repository;

import com.ylab.entity.Transaction;
import com.ylab.entity.User;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class InMemoryTransactionRepository implements TransactionRepository {
    private List<Transaction> transactions = new ArrayList<>();
    /**
     * Находит все транзакции пользователя.
     *
     * @param user пользователь, чьи транзакции нужно найти
     * @return список транзакций
     */
    @Override
    public List<Transaction> findByUser(User user) {
        return transactions.stream().filter(t->t.getUserEmail().equals(user.getEmail()))
                .collect(Collectors.toList());
    }

    /**
     * Сохраняет новую транзакцию.
     *
     * @param transaction транзакция для сохранения
     */
    @Override
    public void save(Transaction transaction) {
        transactions.removeIf(t -> t.getId() == transaction.getId());
        transactions.add(transaction);
    }

    /**
     * Находит транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции
     * @return транзакция или null, если не найдена
     */
    @Override
    public Transaction findById(int id) {
        return transactions.stream()
                .filter(t->t.getId() == id)
                .findFirst()
                .orElse(null);
    }

    /**
     * Удаляет транзакцию по её идентификатору.
     *
     * @param id идентификатор транзакции
     */
    @Override
    public void deleteById(int id) {
        transactions.removeIf(t->t.getId() == id);
    }

}
