package com.ylab.service;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;
import com.ylab.repository.TransactionRepository;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Сервис для управления бизнес-логикой транзакций.
 */
public class TransactionService {
    private final TransactionRepository transactionRepository;

    public TransactionService(TransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /**
     * Создание новой транзакции.
     *
     * @param amount сумма транзакции
     * @param description описание транзакции
     * @param category категория транзакции
     * @param date дата транзакции
     * @param type тип транзакции (доход или расход)
     * @param email почта пользователя
     * @throws IllegalArgumentException если email уже занят или данные некорректны
     */
    public void createTransaction(double amount, String description, String category, LocalDate date
            , TransactionType type, String email) throws SQLException {
        if (amount <= 0) {
            throw new IllegalArgumentException("Сумма транзакций должна быть положительной");
        }
        if (description == null||description.trim().isEmpty()) {
            throw new IllegalArgumentException("Описание не может быть пустым");
        }
        if (category == null||category.trim().isEmpty()) {
            throw new IllegalArgumentException("Категория не может быть пустой");
        }
        if (date == null) {
            throw new IllegalArgumentException("Дата транзакций обязательна");
        }
        if (type == null) {
            throw new IllegalArgumentException("Тип транзакции обязателен");
        }
        Transaction transaction = new Transaction(amount, description, category, date, type, email);
        transactionRepository.save(transaction);
    }

    /**
     * Удаляет транзакцию по её идентификатору.
     *
     * @param transactionId идентификатор транзакции
     * @param currentUser   текущий пользователь (для проверки прав)
     * @throws IllegalArgumentException если транзакция не найдена или нет прав
     */
    public void deleteTransaction(int transactionId, User currentUser) throws SQLException {
        Transaction transaction = transactionRepository.findById(transactionId);
        if (transaction == null) {
            throw new IllegalArgumentException("Транзакция не найдена");
        }
        if (!transaction.getUserEmail().equals(currentUser.getEmail()) && !currentUser.isAdmin()) {
            throw new IllegalArgumentException("Вы можете удалять только свои транзакции");
        }
        transactionRepository.deleteById(transactionId);
    }

    /**
     * Редактирует существующую транзакцию.
     *
     * @param transactionId идентификатор транзакции
     * @param currentUser   текущий пользователь (для проверки прав)
     * @param newAmount     новая сумма (если null, не меняется)
     * @param newDescription новое описание (если null, не меняется)
     * @param newCategory   новая категория (если null, не меняется)
     * @param newDate       новая дата (если null, не меняется)
     * @param newType       новый тип (если null, не меняется)
     * @throws IllegalArgumentException если транзакция не найдена, нет прав или данные некорректны
     */
    public void editTransaction(int transactionId, User currentUser, Double newAmount, String newDescription,
                                String newCategory, LocalDate newDate, TransactionType newType) {
        try {
            Transaction transaction = transactionRepository.findById(transactionId);
            if (transaction == null) {
                throw new IllegalArgumentException("Транзакций не найдено");
            }
            if (!transaction.getUserEmail().equals(currentUser.getEmail()) && !currentUser.isAdmin()) {
                throw new IllegalArgumentException("Вы можете редактировать только свои транзакции");
            }
            if (newAmount != null){
                if(newAmount <= 0){
                    throw new IllegalArgumentException("Сумма должна быть положительна");
                }
                transaction.setAmount(newAmount);
            }
            if (newDescription != null && newDescription.trim().isEmpty()) {
                throw new IllegalArgumentException("Новое описание не может быть пустым");
            }
            if (newDescription != null) {
                transaction.setDescription(newDescription);
            }
            if (newCategory != null && newCategory.trim().isEmpty()) {
                throw new IllegalArgumentException("Новая категория не может быть пустой");
            }
            if (newCategory != null) {
                transaction.setCategory(newCategory);
            }
            if (newDate != null){
                transaction.setDate(newDate);
            }
            if (newType != null){
                transaction.setType(newType);
            }
            transactionRepository.save(transaction);
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }

    }

    /**
     * Возвращает список всех транзакций пользователя с возможностью фильтрации.
     *
     * @param admin      администратор
     * @param targetUser      пользователь, чьи транзакции нужно получить
     * @param startDate начальная дата фильтра (null, если не фильтровать)
     * @param endDate   конечная дата фильтра (null, если не фильтровать)
     * @param category  категория для фильтрации (null, если не фильтровать)
     * @param type      тип транзакции для фильтрации (null, если не фильтровать)
     * @return отфильтрованный список транзакций
     */
    public List<Transaction> getUserTransaction(User admin, User targetUser, LocalDate startDate, LocalDate endDate, String category, TransactionType type) {
        if (!admin.isAdmin() && !admin.getEmail().equals(targetUser.getEmail())) {
            throw new IllegalArgumentException("Вы можете просматривать только свои транзакции");
        }
        List<Transaction> transactions = null;
        try {
            transactions = transactionRepository.findByUser(targetUser);
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return transactions.stream()
                .filter(t -> startDate == null || !t.getDate().isBefore(startDate))
                .filter(t -> endDate == null || !t.getDate().isAfter(endDate))
                .filter(t -> category == null || t.getCategory().equalsIgnoreCase(category))
                .filter(t -> type == null || t.getType() == type)
                .collect(Collectors.toList());
    }

    /**
     * Возвращает транзакции пользователя за указанный период.
     *
     * @param user пользователь
     * @param startDate начальная дата
     * @param endDate конечная дата
     * @return список транзакций
     */
    public List<Transaction> getTransactionsByUserAndPeriod(User user, LocalDate startDate, LocalDate endDate) throws SQLException {
        List<Transaction> transactions = transactionRepository.findByUser(user);
        return transactions.stream()
                .filter(t -> !t.getDate().isBefore(startDate) && !t.getDate().isAfter(endDate))
                .collect(Collectors.toList());
    }

}
