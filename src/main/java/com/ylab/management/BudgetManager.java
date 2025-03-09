package com.ylab.management;

import com.ylab.entity.Budget;
import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;

import java.time.LocalDate;
import java.util.InputMismatchException;

public class BudgetManager {
    private User currentUser;
    TransactionManager transactionManager;
    NotificationManager notificationManager;
    private Budget budget;
    public BudgetManager(User currentUser, TransactionManager transactionManager) {
        this.currentUser = currentUser;
        this.transactionManager = transactionManager;
    }
    public void manageBudget() {
        boolean budgeting = true;
        if(currentUser == null) {
            System.out.println("Пользователь не авторизован");
            return;
        }
        System.out.println("Выберите пункт меню: \n" +
                "1. Задать бюджет\n" +
                "2. Посмотреть бюджет\n" +
                "0. Назад");

        while (budgeting) {
            try {
                int choice = transactionManager.getScanner().nextInt();
                transactionManager.getScanner().nextLine();
                switch (choice) {
                    case 0:
                        budgeting = false;
                    case 1:
                        createBudget();
                        break;
                    case 2:
                        if(currentUser.getBudgets().isEmpty()){
                            System.out.println("Бюджеты не найдены");
                            break;
                        }
                        viewBudget();
                        break;
                }
            }catch (InputMismatchException e){
                transactionManager.getScanner().nextLine();
            }
        }

    }
    private void createBudget() {
        boolean flag = true;
        double limit = 0;
        LocalDate start = null;
        LocalDate end = null;
        while (flag){
        System.out.println("Введите лимит бюджета:");
        limit = transactionManager.getValidAmount();
        System.out.println("Введите начальную дату (yyyy-MM-dd):");
        start = transactionManager.getValidDate();
        System.out.println("Введите конечную дату (yyyy-MM-dd):");
        end = transactionManager.getValidDate();
        if (start.isAfter(end)) {
            System.out.println("Начальная дата не может быть позже конечной");
        }else
            flag = false;
        }
        Budget budget = new Budget(limit,start,end);
        currentUser.getBudgets().add(budget);
        System.out.println("Бюджет установлен!");
    }
    private void viewBudget() {
        for (Budget budget : currentUser.getBudgets()) {
            System.out.println(budget.toString());
        }
        double totalIncome = 0; // доходы
        double totalExpense = 0; // расходы
        for (Transaction t : currentUser.getTransactions()) {
            if(t.getDate().isAfter(budget.getStart()) && t.getDate().isBefore(budget.getEnd())
                    || t.getDate().equals(budget.getStart()) || t.getDate().equals(budget.getEnd())){
                if (t.getType() == TransactionType.INCOME){
                    totalIncome += t.getAmount();
                }else if (t.getType() == TransactionType.EXPENSE){
                    totalExpense += t.getAmount();
                }
            }
        }
        double remaining = budget.getLimit() - totalIncome;
        System.out.println("Лимит: "+budget.getLimit()+"\n" +
                "Доходы: "+totalIncome+ "\n" +
                "Расходы: "+totalExpense+ "\n" +
                "Остаток: "+remaining );
        if (remaining < 0) {
            String message = "Бюджет " + budget.getLimit() + " превышен. Остаток: " + String.format("%.2f", remaining);
            notificationManager.logNotification(message);
        }
        System.out.println("-----------------");
    }
}
