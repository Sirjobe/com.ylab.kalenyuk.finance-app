package com.ylab.entityTest;

import com.ylab.entity.Goal;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class GoalTest {

    @Test
    void testValidGoalCreation() {
        Goal goal = new Goal(500.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Vacation", "test@example.com");
        assertEquals(500.0, goal.getTargetAmount());
        assertEquals(LocalDate.of(2025, 1, 1), goal.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), goal.getEndDate());
        assertEquals("Vacation", goal.getDescription());
        assertEquals("test@example.com", goal.getEmail());
        assertEquals(0, goal.getId()); // ID по умолчанию 0
    }

    @Test
    void testSettersAndGetters() {
        Goal goal = new Goal();
        goal.setId(1);
        goal.setTargetAmount(1000.0);
        goal.setStartDate(LocalDate.of(2025, 1, 1));
        goal.setEndDate(LocalDate.of(2025, 12, 31));
        goal.setDescription("Car");
        goal.setEmail("new@example.com");

        assertEquals(1, goal.getId());
        assertEquals(1000.0, goal.getTargetAmount());
        assertEquals(LocalDate.of(2025, 1, 1), goal.getStartDate());
        assertEquals(LocalDate.of(2025, 12, 31), goal.getEndDate());
        assertEquals("Car", goal.getDescription());
        assertEquals("new@example.com", goal.getEmail());
    }
}