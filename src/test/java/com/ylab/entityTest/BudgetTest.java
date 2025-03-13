package com.ylab.entityTest;

import com.ylab.entity.Budget;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;

public class BudgetTest {

    @Test
    void testValidBudgetCreation() {
        Budget budget = new Budget(1000.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), "test@example.com");
        assertEquals(1000.0, budget.getLimit());
        assertEquals(LocalDate.of(2023, 1, 1), budget.getStart());
        assertEquals(LocalDate.of(2023, 1, 31), budget.getEnd());
        assertEquals("test@example.com", budget.getEmail());
        assertEquals(1, budget.getId()); // Проблема с генерацией ID, всегда 1
    }

    @Test
    void testInvalidDateRangeThrowsException() {
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                new Budget(1000.0, LocalDate.of(2023, 2, 1), LocalDate.of(2023, 1, 31), "test@example.com"));
        assertEquals("Дата начала не может быть после даты конца", exception.getMessage());
    }
}
