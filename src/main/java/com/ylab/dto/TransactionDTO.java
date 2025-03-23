package com.ylab.dto;

import com.ylab.entity.TransactionType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class TransactionDTO {
    private int id;

    @Positive(message = "Сумма должна быть положительной")
    private double amount;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotBlank(message = "Категория не может быть пустой")
    private String category;

    @NotNull(message = "Дата обязательна")
    private LocalDate date;

    @NotNull(message = "Тип обязателен")
    private TransactionType type;

    @NotBlank(message = "Email пользователя обязателен")
    private String email;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }
    public TransactionType getType() { return type; }
    public void setType(TransactionType type) { this.type = type; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}