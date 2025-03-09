package com.ylab.entity;

import java.time.LocalDate;

public class Transaction {
    private double amount;
    private String description;
    private String category;
    private LocalDate date;
    private TransactionType type;
    private static int nextId = 1;
    private int id;

    public Transaction(double amount, String description, String category, LocalDate date, TransactionType type) {
        this.id = nextId++;
        this.amount = amount;
        this.description = description;
        this.category = category;
        this.date = date;
        this.type = type;
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

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "Сумма транзакции =" + amount +
                ", Категория ='" + category + '\'' +
                ", Дата транзакции ='" + date + '\'' +
                ", Тип =" + type +
                ", Описание =" + description +
                '}';
    }
}
