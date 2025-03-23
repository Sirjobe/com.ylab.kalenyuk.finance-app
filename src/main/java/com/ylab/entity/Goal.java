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
    private static int nextId = 1;
    private final int id;
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
        this.id = nextId++;
        this.targetAmount = targetAmount;
        this.startDate = startDate;
        this.endDate = endDate;
        this.description = description;
        this.email = email;
    }

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

    public int getId() {
        return id;
    }

    public String getEmail() {return email;}

}
