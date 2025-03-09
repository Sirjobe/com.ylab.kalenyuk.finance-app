package com.ylab.entity;

import java.time.LocalDate;

public class Goal {
    private double targetAmount;
    private LocalDate deadline;
    private String description;
    public Goal(double targetAmount, LocalDate deadline, String description) {
        this.targetAmount = targetAmount;
        this.deadline = deadline;
        this.description = description;
    }

    public double getTargetAmount() {
        return targetAmount;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public LocalDate getDeadline() {
        return deadline;
    }

    public void setDeadline(LocalDate deadline) {
        this.deadline = deadline;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Цели{" +
                "Целевая сумма=" + targetAmount +
                ", Дата завершения=" + deadline +
                ", Описание='" + description + '\'' +
                '}';
    }
}
