package com.ylab.repositoryTest;

import com.ylab.entity.Budget;
import com.ylab.entity.User;
import com.ylab.repository.InMemoryBudgetRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryBudgetRepositoryTest {

    private InMemoryBudgetRepository repository;
    private User user;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBudgetRepository();
        user = new User("test@example.com", "testUser", "password123", false);
    }


    @Test
    void testSaveAndFindById() {
        Budget budget = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), user.getEmail());
        repository.save(budget);
        assertEquals(1, budget.getId()); // ID должен быть 1 после сохранения

        Budget found = repository.findById(1);
        assertNotNull(found);
        assertEquals(1000.0, found.getLimit());
        assertEquals(user.getEmail(), found.getEmail());
    }

    @Test
    void testFindByIdNotFound() {
        Budget found = repository.findById(999);
        assertNull(found);
    }

    @Test
    void testFindByUser() {
        Budget budget1 = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), user.getEmail());
        Budget budget2 = new Budget(2000.0, LocalDate.of(2025, 2, 1), LocalDate.of(2025, 2, 28), user.getEmail());
        repository.save(budget1);
        repository.save(budget2);

        List<Budget> budgets = repository.findByUser(user);
        System.out.println("Budgets: " + budgets); // Отладка
        assertEquals(2, budgets.size());
        assertTrue(budgets.contains(budget1));
        assertTrue(budgets.contains(budget2));
    }

    @Test
    void testDeleteById() {
        Budget budget = new Budget(1000.0, LocalDate.of(2025, 1, 1), LocalDate.of(2025, 1, 31), user.getEmail());
        repository.save(budget);
        repository.deleteById(budget.getId());

        Budget found = repository.findById(budget.getId());
        assertNull(found);
    }

    @Test
    void testDeleteByIdNotFound() {
        repository.deleteById(999); // Удаление несуществующего ID не должно вызывать ошибку
        assertNull(repository.findById(999));
    }
}