//package com.ylab.repositoryTest;
//
//import com.ylab.entity.User;
//import com.ylab.repository.InMemoryUserRepository;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.util.List;
//import static org.junit.jupiter.api.Assertions.*;
//
//public class InMemoryUserRepositoryTest {
//
//    private InMemoryUserRepository repository;
//
//    @BeforeEach
//    void setUp() {
//        repository = new InMemoryUserRepository();
//    }
//
//    @Test
//    void testSaveAndFindByEmail() {
//        User user = new User("test@example.com", "testUser", "password123", false);
//        repository.save(user);
//        User found = repository.findByEmail("test@example.com");
//        assertNotNull(found);
//        assertEquals("testUser", found.getUsername());
//    }
//
//    @Test
//    void testDeleteByEmail() {
//        User user = new User("test@example.com", "testUser", "password123", false);
//        repository.save(user);
//        repository.deleteByEmail("test@example.com");
//        assertNull(repository.findByEmail("test@example.com"));
//    }
//
//    @Test
//    void testFindAll() {
//        User user1 = new User("test1@example.com", "user1", "password123", false);
//        User user2 = new User("test2@example.com", "user2", "password123", false);
//        repository.save(user1);
//        repository.save(user2);
//        List<User> users = repository.findAll();
//        assertEquals(2, users.size());
//    }
//}