//package com.ylab.entityTest;
//
//import com.ylab.entity.Goal;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//
//public class GoalTest {
//
//    @BeforeEach
//    void setUp() {
//        // Сбрасываем статический счетчик ID перед каждым тестом
//        try {
//            java.lang.reflect.Field idField = Goal.class.getDeclaredField("nextId");
//            idField.setAccessible(true);
//            idField.set(null, 1);
//        } catch (Exception e) {
//            throw new RuntimeException("Не удалось сбросить ID", e);
//        }
//    }
//
//    @Test
//    void testValidGoalCreation() {
//        Goal goal1 = new Goal(500.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Vacation", "test@example.com");
//        assertEquals(500.0, goal1.getTargetAmount());
//        assertEquals("Vacation", goal1.getDescription());
//        assertEquals("test@example.com", goal1.getEmail());
//        assertEquals(1, goal1.getId());
//
//        // Проверяем, что ID увеличивается
//        Goal goal2 = new Goal(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Car", "test@example.com");
//        assertEquals(2, goal2.getId());
//    }
//}