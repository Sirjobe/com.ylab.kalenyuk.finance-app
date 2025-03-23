package com.ylab.serviceTest;

import com.ylab.entity.Goal;
import com.ylab.entity.User;
import com.ylab.repository.GoalRepository;
import com.ylab.service.GoalService;
import com.ylab.service.StatisticsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
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
        user = new User("test@example.com", "testUser", "password123", false);
    }

    @Test
    void testSetGoalSuccess() throws SQLException {
        LocalDate futureDate = LocalDate.now().plusDays(30);

        doNothing().when(goalRepository).save(any(Goal.class));

        goalService.setGoal(user, 1000.0, "Vacation", futureDate);

        ArgumentCaptor<Goal> goalCaptor = ArgumentCaptor.forClass(Goal.class);
        verify(goalRepository).save(goalCaptor.capture());

        Goal capturedGoal = goalCaptor.getValue();
        assertEquals(1000.0, capturedGoal.getTargetAmount());
        assertEquals("Vacation", capturedGoal.getDescription());
        assertEquals(futureDate, capturedGoal.getEndDate(), "endDate should match futureDate");
        assertEquals(LocalDate.now(), capturedGoal.getStartDate(), "startDate should match current date");
        assertEquals(user.getEmail(), capturedGoal.getEmail());
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
    void testDeleteGoalSuccess() throws SQLException {
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        goal.setId(1);
        when(goalRepository.findById(1)).thenReturn(goal);

        goalService.deleteGoal(1, user);
        verify(goalRepository).deleteById(1);
    }

    @Test
    void testDeleteGoalNotFound() throws SQLException {
        when(goalRepository.findById(1)).thenReturn(null);
        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.deleteGoal(1, user));
        assertEquals("Цель не найдена", exception.getMessage());
    }

    @Test
    void testDeleteGoalNoPermission() throws SQLException {
        User otherUser = new User("other@example.com", "otherUser", "password123", false);
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        goal.setId(1);
        when(goalRepository.findById(1)).thenReturn(goal);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.deleteGoal(1, otherUser));
        assertEquals("Вы можете удалять только свои цели", exception.getMessage());
    }

    @Test
    void testGetUserGoals() throws SQLException {
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        when(goalRepository.findByUser(user)).thenReturn(Collections.singletonList(goal));

        List<Goal> goals = goalService.getUserGoals(user);
        assertEquals(1, goals.size());
        assertEquals(goal, goals.get(0));
    }

    @Test
    void testTrackGoalProgress() throws SQLException {
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        goal.setId(1);
        when(goalRepository.findById(1)).thenReturn(goal);
        when(statisticsService.calculateCurrentBalance(user, user)).thenReturn(600.0);

        String progress = goalService.trackGoalProgress(1, user, user);
        assertTrue(progress.contains("Достигнуто: 600.00"));
        assertTrue(progress.contains("Прогресс: 60.00%"));
        assertTrue(progress.contains("Осталось накопить: 400.00"));
    }

    @Test
    void testTrackGoalProgressGoalAchieved() throws SQLException {
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        goal.setId(1);
        when(goalRepository.findById(1)).thenReturn(goal);
        when(statisticsService.calculateCurrentBalance(user, user)).thenReturn(1200.0);

        String progress = goalService.trackGoalProgress(1, user, user);
        assertTrue(progress.contains("Достигнуто: 1000.00"));
        assertTrue(progress.contains("Прогресс: 100.00%"));
        assertTrue(progress.contains("Цель достигнута!"));
    }

    @Test
    void testTrackGoalProgressNoPermission() throws SQLException {
        User otherUser = new User("other@example.com", "otherUser", "password123", false);
        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
        goal.setId(1);
        when(goalRepository.findById(1)).thenReturn(goal);

        Exception exception = assertThrows(IllegalArgumentException.class, () ->
                goalService.trackGoalProgress(1, otherUser, otherUser));
        assertEquals("Вы можете отслеживать только свои цели", exception.getMessage());
    }
}