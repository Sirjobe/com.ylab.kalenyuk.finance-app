package com.ylab.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.time.LocalDate;

public class BudgetDTO {
    private int id;

    @Positive(message = "Лимит должен быть положительным")
    private double limit;

    @NotNull(message = "Начальная дата обязательна")
    private LocalDate start;

    @NotNull(message = "Конечная дата обязательна")
    private LocalDate end;

    @NotBlank(message = "Email пользователя обязателен")
    private String email;

    // Геттеры и сеттеры
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getLimit() { return limit; }
    public void setLimit(double limit) { this.limit = limit; }
    public LocalDate getStart() { return start; }
    public void setStart(LocalDate start) { this.start = start; }
    public LocalDate getEnd() { return end; }
    public void setEnd(LocalDate end) { this.end = end; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}