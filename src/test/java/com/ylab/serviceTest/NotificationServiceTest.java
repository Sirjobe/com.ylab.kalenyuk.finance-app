//package com.ylab.serviceTest;
//
//import com.ylab.entity.Budget;
//import com.ylab.entity.Goal;
//import com.ylab.entity.User;
//import com.ylab.service.BudgetService;
//import com.ylab.service.EmailSender;
//import com.ylab.service.GoalService;
//import com.ylab.service.NotificationService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//
//import java.time.LocalDate;
//import java.util.Collections;
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class NotificationServiceTest {
//
//    @Mock
//    private BudgetService budgetService;
//
//    @Mock
//    private GoalService goalService;
//
//    @Mock
//    private EmailSender emailSender;
//
//    @InjectMocks
//    private NotificationService notificationService;
//
//    private User user;
//
//    @BeforeEach
//    void setUp() {
//        MockitoAnnotations.openMocks(this);
//        user = new User("test@example.com", "testUser", "password123", false);
//    }
//
//    @Test
//    void testCheckBudgetsAndNotify() {
//        Budget budget = new Budget(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), user.getEmail());
//        when(budgetService.getUserBudgets(user, user)).thenReturn(Collections.singletonList(budget));
//        when(budgetService.checkBudget(budget.getId(), user, user)).thenReturn("Бюджет превышен!");
//        List<String> notifications = notificationService.checkBudgetsAndNotify(user, user);
//        assertEquals(1, notifications.size());
//        assertEquals("Бюджет превышен!", notifications.get(0));
//        verify(emailSender).sendEmail(user.getEmail(), "Превышение бюджета", "Бюджет превышен!");
//    }
//
//    @Test
//    void testCheckGoalsAndNotify() {
//        Goal goal = new Goal(1000.0, LocalDate.now(), LocalDate.now().plusDays(30), "Vacation", user.getEmail());
//        when(goalService.getUserGoals(user)).thenReturn(Collections.singletonList(goal));
//        when(goalService.trackGoalProgress(goal.getId(), user, user)).thenReturn("Цель достигнута!\n...");
//        List<String> notifications = notificationService.checkGoalsAndNotify(user, user);
//        assertEquals(1, notifications.size());
//        assertTrue(notifications.get(0).contains("Цель достигнута"));
//        verify(emailSender).sendEmail(user.getEmail(), "Достижение цели", "Поздравляем! Цель достигнута!\n...");
//    }
//
//    @Test
//    void testNoNotifications() {
//        when(budgetService.getUserBudgets(user, user)).thenReturn(Collections.emptyList());
//        when(goalService.getUserGoals(user)).thenReturn(Collections.emptyList());
//        List<String> budgetNotifications = notificationService.checkBudgetsAndNotify(user, user);
//        List<String> goalNotifications = notificationService.checkGoalsAndNotify(user, user);
//        assertTrue(budgetNotifications.isEmpty());
//        assertTrue(goalNotifications.isEmpty());
//    }
//}