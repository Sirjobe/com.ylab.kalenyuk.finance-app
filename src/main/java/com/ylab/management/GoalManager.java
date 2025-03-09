package com.ylab.management;

import com.ylab.entity.Goal;
import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;

import java.time.LocalDate;
import java.util.InputMismatchException;

public class GoalManager {
    private User currentUser;
    private TransactionManager transactionManager;
    private Goal goal;
    private NotificationManager notificationManager;
    public GoalManager(User currentUser, TransactionManager transactionManager) {
        this.currentUser = currentUser;
        this.transactionManager = transactionManager;
    }
    public void manageGoals(){
        if(currentUser==null){
            System.out.println("Пользователь не авторизирован");
            return;
        }
        System.out.println("Выберите пункт меню: \n" +
                "1. Добавить цель\n" +
                "2. Просмотреть цели\n" +
                "0. Назад");
        boolean budgeting = true;
        while(budgeting){
            try {
                int choice = transactionManager.getScanner().nextInt();
                transactionManager.getScanner().nextLine();
                switch (choice){
                    case 0:
                        budgeting = false;
                    case 1:
                        if(currentUser.getGoals().isEmpty()){
                            System.out.println("Цели не заданы");
                            return;
                        }
                        addGoal();
                        break;
                    case 2:
                        viewGoals();
                        break;
                }
            }catch (InputMismatchException e){
                System.out.println("Введите число");
                transactionManager.getScanner().nextLine();
            }
        }
    }
    private void addGoal(){
        boolean flag = true;
        LocalDate deadLine = null;

        System.out.println("Введите сумму цели:");
        double targetAmount = transactionManager.getValidAmount();

        while (flag){
            System.out.println("Введите дедлайн (yyyy-MM-dd):");
            deadLine = transactionManager.getValidDate();
            if (!deadLine.isBefore(LocalDate.now())) {
                flag = false;
            }else
                System.out.println("Дедлайн не может быть в прошлом");
        }

        System.out.println("Введите описание или \"Enter\" для пропуск:");
        String description = transactionManager.getScanner().nextLine();
        if(description.isEmpty()){
            description="Без описания";
        }

        Goal goal = new Goal(targetAmount, deadLine, description);
        currentUser.getGoals().add(goal);
        System.out.println("Цель добавлена");
    }
    private void viewGoals(){
        double progress = 0;
        for (Goal goal : currentUser.getGoals()) {
            System.out.println(goal.toString());
        }
        for (Transaction t : currentUser.getTransactions()) {
            if(t.getType() == TransactionType.INCOME && !t.getDate().isAfter(goal.getDeadline())){
                progress += t.getAmount();
            }
        }
        System.out.println("Прогресс: " + progress+"из"+goal.getTargetAmount()+"/n" +
                "Осталось: "+(goal.getTargetAmount()-progress));
        if (progress >= goal.getTargetAmount()) {
            String message = "Цель достигнута: " + goal.getDescription();
            notificationManager.logNotification(message);
        } else if (LocalDate.now().isAfter(goal.getDeadline()) && progress < goal.getTargetAmount()) {
            String message = "Цель просрочена: " + goal.getDescription();
            notificationManager.logNotification(message);
        }
        System.out.println("-------------");


    }
}
