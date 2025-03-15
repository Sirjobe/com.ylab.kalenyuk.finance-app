package com.ylab.entity;

import java.time.LocalDate;

/**
 * Класс, представляющий финансовую транзакцию.
 */
public class Transaction {
    private double amount;
    private String description;
    private String category;
    private LocalDate date;
    private TransactionType type;
    private int id;
    private String email;

    /**
     * Создает новую транзакцию.
     *
     * @param amount сумма транзакции
     * @param description описание транзакции
     * @param category категория транзакции
     * @param date дата транзакции
     * @param type тип транзакции (доход или расход)
     * @param email почта пользователя
     */
    public Transaction(double amount, String description, String category, LocalDate date, TransactionType type, String email) {
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
        this.type = type;
        this.email = email;
    }

    public Transaction() {}

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public TransactionType getType() {
        return type;
    }

    public String getTypeString() {
        return type.toString();
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getUserEmail() {return email;}

    @Override
    public String toString() {
        return "ID "+getId()+" {" +
                "Сумма транзакции =" + amount +
                ", Категория ='" + category + '\'' +
                ", Дата транзакции ='" + date + '\'' +
                ", Тип =" + type +
                ", Описание =" + description +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
