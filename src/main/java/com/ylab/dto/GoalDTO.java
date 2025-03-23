package com.ylab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class GoalDTO {
    private int id;

    @Positive(message = "Целевая сумма должна быть положительной")
    private double targetAmount;

    @NotNull(message = "Начальная дата обязательна")
    private LocalDate startDate;

    @NotNull(message = "Конечная дата обязательна")
    private LocalDate endDate;

    @NotBlank(message = "Описание не может быть пустым")
    private String description;

    @NotBlank(message = "Email пользователя обязателен")
    private String email;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getTargetAmount() { return targetAmount; }
    public void setTargetAmount(double targetAmount) { this.targetAmount = targetAmount; }
    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}