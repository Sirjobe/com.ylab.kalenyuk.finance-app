//package com.ylab.repositoryTest;
//
//import com.ylab.entity.Budget;
//import com.ylab.entity.User;
//import com.ylab.repository.InMemoryBudgetRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.time.LocalDate;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class InMemoryBudgetRepositoryTest {
//
//    private InMemoryBudgetRepository repository;
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        repository = new InMemoryBudgetRepository();
//        user = new User("test@example.com", "testUser", "password123", false);
//    }
//
//    @Test
//    void testSaveAndFindById() {
//        Budget budget = new Budget(1000.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), user.getEmail());
//        repository.save(budget);
//        Budget found = repository.findById(1);
//        assertNotNull(found);
//        assertEquals(1000.0, found.getLimit());
//    }
//
//    @Test
//    void testFindByUser() {
//        Budget budget = new Budget(1000.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), user.getEmail());
//        repository.save(budget);
//        List<Budget> budgets = repository.findByUser(user);
//        assertEquals(1, budgets.size());
//        assertEquals(budget, budgets.get(0));
//    }
//
//    @Test
//    void testDeleteById() {
//        Budget budget = new Budget(1000.0, LocalDate.of(2023, 1, 1), LocalDate.of(2023, 1, 31), user.getEmail());
//        repository.save(budget);
//        repository.deleteById(1);
//        assertNull(repository.findById(1));
//    }
//}