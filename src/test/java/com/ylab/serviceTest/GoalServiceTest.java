package com.ylab.serviceTest;

import com.ylab.entity.Goal;
import com.ylab.entity.User;
import com.ylab.repository.GoalRepository;
import com.ylab.service.GoalService;
import com.ylab.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class GoalServiceTest {

    @Mock
    private GoalRepository goalRepository;

    @Mock
    private StatisticsService statisticsService;

    @InjectMocks
    private GoalService goalService;

    private User user;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        user = new User("test@example.com", "testUser", "password123", false);
        try {
            java.lang.reflect.Field idField = Goal.class.getDeclaredField("nextId"); // Исправлено на "nextId"
            idField.setAccessible(true);
            idField.set(null, 1);
        } catch (Exception e) {
            throw new RuntimeException("Не удалось сбросить ID", e);
        }
    }

    @Test
    void testSetGoalSuccess() {
        LocalDate futureDate = LocalDate.now().plusDays(30); // Будущая дата
        goalService.setGoal(user, 1000.0, "Vacation", futureDate);
        verify(goalRepository).save(argThat(goal ->
                goal.getTargetAmount() == 1000.0 &&
                        goal.getDescription().equals("Vacation") &&
                        goal.getEmail().equals(user.getEmail())));
    }

    @Test
    void testSetGoalInvalidAmount() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.setGoal(user, -1000.0, "Vacation", futureDate));
        assertEquals("Целевая сумма должна быть положительной", exception.getMessage());
    }

    @Test
    void testSetGoalEmptyDescription() {
        LocalDate futureDate = LocalDate.now().plusDays(30);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.setGoal(user, 1000.0, "", futureDate));
        assertEquals("Описание цели не может быть пустым", exception.getMessage());
    }

    @Test
    void testSetGoalPastEndDate() {
        LocalDate pastDate = LocalDate.now().minusDays(1);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.setGoal(user, 1000.0, "Vacation", pastDate));
        assertEquals("Дата окончания не может быть в прошлом", exception.getMessage());
    }

    @Test
    void testDeleteGoalSuccess() {
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        when(goalRepository.findById(1)).thenReturn(goal);
        goalService.deleteGoal(1, user);
        verify(goalRepository).deleteById(1);
    }

    @Test
    void testDeleteGoalNotFound() {
        when(goalRepository.findById(1)).thenReturn(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.deleteGoal(1, user));
        assertEquals("Цель не найдена", exception.getMessage());
    }

    @Test
    void testDeleteGoalNoPermission() {
        User otherUser = new User("other@example.com", "otherUser", "password123", false);
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        when(goalRepository.findById(1)).thenReturn(goal);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.deleteGoal(1, otherUser));
        assertEquals("Вы можете удалять только свои цели", exception.getMessage());
    }

    @Test
    void testGetUserGoals() {
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        when(goalRepository.findByUser(user)).thenReturn(Collections.singletonList(goal));
        List<Goal> goals = goalService.getUserGoals(user);
        assertEquals(1, goals.size());
        assertEquals(goal, goals.get(0));
    }

    @Test
    void testTrackGoalProgress() {
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        when(goalRepository.findById(1)).thenReturn(goal);
        when(statisticsService.calculateCurrentBalance(user, user)).thenReturn(600.0);
        String progress = goalService.trackGoalProgress(1, user, user);
        assertTrue(progress.contains("Достигнуто: 600.00"), "Прогресс должен содержать достигнутую сумму");
        assertTrue(progress.contains("Прогресс: 60.00%"), "Прогресс должен содержать процент");
        assertTrue(progress.contains("Осталось накопить: 400.00"), "Прогресс должен содержать остаток");
    }

    @Test
    void testTrackGoalProgressGoalAchieved() {
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        when(goalRepository.findById(1)).thenReturn(goal);
        when(statisticsService.calculateCurrentBalance(user, user)).thenReturn(1200.0);
        String progress = goalService.trackGoalProgress(1, user, user);
        assertTrue(progress.contains("Достигнуто: 1000.00"), "Прогресс должен быть ограничен целевой суммой");
        assertTrue(progress.contains("Прогресс: 100.00%"), "Прогресс должен быть 100%");
        assertTrue(progress.contains("Цель достигнута!"), "Должно быть указано достижение цели");
    }

    @Test
    void testTrackGoalProgressNoPermission() {
        User otherUser = new User("other@example.com", "otherUser", "password123", false);
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        when(goalRepository.findById(1)).thenReturn(goal);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.trackGoalProgress(1, otherUser, otherUser));
        assertEquals("Вы можете отслеживать только свои цели", exception.getMessage());
    }
}