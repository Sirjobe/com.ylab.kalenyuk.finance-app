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
        Goal goal = new Goal(500.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "Vacation", user.getEmail());
        repository.save(goal);
        Goal found = repository.findById(goal.getId()); // Используем ID конкретной цели
        assertNotNull(found);
        assertEquals(500.0, found.getTargetAmount());
        assertEquals("Vacation", found.getDescription());
    }

    @Test
    void testFindByUser() {
        Goal goal = new Goal(500.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "Vacation", user.getEmail());
        repository.save(goal);
        List<Goal> goals = repository.findByUser(user);
        assertEquals(1, goals.size());
        assertEquals(goal, goals.get(0));
    }

    @Test
    void testDeleteById() {
        Goal goal = new Goal(500.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "Vacation", user.getEmail());
        repository.save(goal);
        repository.deleteById(1);
        assertNull(repository.findById(1));
    }

    @Test
    void testUpdateGoal() {
        Goal goal = new Goal(500.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "Vacation", user.getEmail());
        repository.save(goal);
        int id = goal.getId();
        Goal updatedGoal = new Goal(1000.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 12, 31), "New Vacation", user.getEmail());
        repository.save(updatedGoal);
        Goal found = repository.findById(id);
        assertEquals(500.0, found.getTargetAmount());
    }
}