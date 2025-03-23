package com.ylab.entity;

import java.time.LocalDate;

/**
 * Класс, представляющий финансовую цель пользователя.
 */
public class Goal {
    private double targetAmount;
    private LocalDate startDate;
    private LocalDate endDate;
    private String description;
    private int id;
    private String email;

    /**
     * Создает новую финансовую цель.
     *
     * @param targetAmount целевая сумма
     * @param startDate начало
     * @param endDate крайний срок достижения цели
     * @param description описание цели
     */
    public Goal(double targetAmount, LocalDate startDate, LocalDate endDate, String description, String email) {
        this.targetAmount = targetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.email = email;
    }

    public Goal() {}

    /**
     * Возвращает целевую сумму цели.
     *
     * @return целевая сумма
     */
    public double getTargetAmount() {
        return targetAmount;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setTargetAmount(double targetAmount) {
        this.targetAmount = targetAmount;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public String getEmail() {return email;}

    @Override
    public String toString() {
        return "ID: "+getId()+" {" +
                "Целевая сумма=" + targetAmount +
                ", Дата завершения=" + endDate +
                ", Описание='" + description + '\'' +
                '}';
    }
}
