package com.ylab.entity;

import java.time.LocalDate;

/**
 * Класс, представляющий бюджет пользователя.
 */
public class Budget {
    private double limit = 0;
    private LocalDate start;
    private LocalDate end;
    private int id;
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
        this.limit = limit;
        this.start = start;
        this.end = end;
        this.email = email;
    }

    public Budget() {}

    public double getLimit() {
        return limit;
    }

    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    public String getEmail() {return email;}

    public void setId(int id) {
        this.id = id;
    }

    public void setLimit(double limit) {
        this.limit = limit;
    }

    public void setStart(LocalDate start) {
        this.start = start;
    }

    public void setEnd(LocalDate end) {
        this.end = end;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {return id;}

    @Override
    public String toString() {
        return "ID: "+getId()+" Бюджет: " + limit + " | " + start + " - " + end;
    }

}
