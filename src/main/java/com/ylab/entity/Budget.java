package com.ylab.entity;

import java.time.LocalDate;

/**
 * Класс, представляющий бюджет пользователя.
 */
public class Budget {
    private double limit = 0;
    private LocalDate start;
    private LocalDate end;
    private static int id = 1;
    private String email;

    /**
     * Создает новый бюджет.
     *
     * @param limit лимит бюджета
     * @param start начальная дата бюджета
     * @param end конечная дата бюджета
     * @throws IllegalArgumentException если начальная дата позже конечной
     * @param email почта пользователя
     */
    public Budget(double limit, LocalDate start, LocalDate end, String email) {
        if (start.isAfter(end)){
            throw new IllegalArgumentException("Дата начала не может быть после даты конца");
        }
        this.id = id++;
        this.limit = limit;
        this.start = start;
        this.end = end;
        this.email = email;
    }

    /**
     * Возвращает лимит бюджета.
     *
     * @return лимит бюджета
     */
    public double getLimit() {
        return limit;
    }

    /**
     * Возвращает начальную дату бюджета.
     *
     * @return начальная дата
     */
    public LocalDate getStart() {
        return start;
    }

    /**
     * Возвращает конечную дату бюджета.
     *
     * @return конечная дата
     */
    public LocalDate getEnd() {
        return end;
    }

    public String getEmail() {return email;}

    public int getId() {return id;}

    @Override
    public String toString() {
        return "Бюджет: " + limit + " | " + start + " - " + end;
    }

}
