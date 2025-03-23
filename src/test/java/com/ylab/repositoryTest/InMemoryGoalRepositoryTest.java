package com.ylab.repositoryTest;

import com.ylab.entity.Goal;
import com.ylab.entity.User;
import com.ylab.repository.InMemoryGoalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryGoalRepositoryTest {

    private InMemoryGoalRepository repository;
    private User user;

    @BeforeEach
    void setUp() {
        repository = new InMemoryGoalRepository();
        user = new User("test@example.com", "testUser", "password123", false);
    }

    @Test
    void testSaveAndFindById() {
        Goal goal = new Goal(500.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Vacation", user.getEmail());
        repository.save(goal);
        assertEquals(1, goal.getId());

        Goal found = repository.findById(1);
        assertNotNull(found);
        assertEquals(500.0, found.getTargetAmount());
        assertEquals("Vacation", found.getDescription());
    }

    @Test
    void testFindByIdNotFound() {
        Goal found = repository.findById(999);
        assertNull(found);
    }

    @Test
    void testFindByUser() {
        Goal goal1 = new Goal(500.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Vacation", user.getEmail());
        Goal goal2 = new Goal(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Car", user.getEmail());
        repository.save(goal1);
        repository.save(goal2);

        List<Goal> goals = repository.findByUser(user);
        System.out.println("Goals: " + goals); // Отладка
        assertEquals(2, goals.size());
        assertTrue(goals.contains(goal1));
        assertTrue(goals.contains(goal2));
    }

    @Test
    void testDeleteById() {
        Goal goal = new Goal(500.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 12, 31), "Vacation", user.getEmail());
        repository.save(goal);
        repository.deleteById(goal.getId());

        Goal found = repository.findById(goal.getId());
        assertNull(found);
    }

    @Test
    void testDeleteByIdNotFound() {
        repository.deleteById(999); // Удаление несуществующего ID не должно вызывать ошибку
        assertNull(repository.findById(999));
    }
}