package com.ylab.entity;

import java.time.LocalDate;

public class Budget {
    private double limit = 0;
    private LocalDate start;
    private LocalDate end;

    public Budget(double limit, LocalDate start, LocalDate end) {
        if (start.isAfter(end)){
            throw new IllegalArgumentException("Дата начала не может быть после даты конца");
        }
        this.limit = limit;
        this.start = start;
        this.end = end;
    }

    public double getLimit() {
        return limit;
    }


    public LocalDate getStart() {
        return start;
    }

    public LocalDate getEnd() {
        return end;
    }

    @Override
    public String toString() {
        return "Бюджет: " + limit + " | " + start + " - " + end;
    }
}
