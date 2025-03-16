package com.ylab.entityTest;

import com.ylab.entity.Budget;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class BudgetTest {

    @Test
    void testValidBudgetCreation() {
        Budget budget = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), "test@example.com");
        assertEquals(1000.0, budget.getLimit());
        assertEquals(LocalDate.of(2025, 1, 1), budget.getStart());
        assertEquals(LocalDate.of(2025, 1, 31), budget.getEnd());
        assertEquals("test@example.com", budget.getEmail());
        assertEquals(0, budget.getId()); // ID по умолчанию 0, так как не устанавливается в конструкторе
    }

    @Test
    void testInvalidDateRangeThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Budget(1000.0, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 1, 31), "test@example.com"));
        assertEquals("Дата начала не может быть после даты конца", exception.getMessage());
    }

    @Test
    void testSettersAndGetters() {
        Budget budget = new Budget();
        budget.setId(1);
        budget.setLimit(2000.0);
        budget.setStart(LocalDate.of(2025, 1, 1));
        budget.setEnd(LocalDate.of(2025, 1, 31));
        budget.setEmail("new@example.com");

        assertEquals(1, budget.getId());
        assertEquals(2000.0, budget.getLimit());
        assertEquals(LocalDate.of(2025, 1, 1), budget.getStart());
        assertEquals(LocalDate.of(2025, 1, 31), budget.getEnd());
        assertEquals("new@example.com", budget.getEmail());
    }
}