package com.ylab.management;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.InputMismatchException;
import java.util.Map;


public class StatisticsManager {
    private User currentUser;
    private TransactionManager transactionManager;
    public StatisticsManager(User currentUser, TransactionManager transactionManager) {
        this.currentUser = currentUser;
        this.transactionManager = transactionManager;
    }
    public void showStatistics() {
        if (transactionManager != null) {
            System.out.println("Пользователь не авторизирован");
            return;
        }
        if (currentUser.getTransactions().isEmpty()) {
            System.out.println("Транзакций нет");
            return;
        }
        System.out.println("Статистика за: \n" +
                "1. Весь период\n" +
                "2. Произвольный период\n" +
                "3. По категориям" +
                "0. Назад");
        boolean flag = true;
        while (flag) {
            try {
                int choice = transactionManager.getScanner().nextInt();
                transactionManager.getScanner().nextLine();
                switch (choice) {
                    case 0:
                        flag = false;
                    case 1:
                        wholePeriod();
                        break;
                    case 2:
                        currentPeriod();
                        break;
                    case 3:
                        categoryStatistics();

                        break;
                }
            }catch (InputMismatchException e){
                System.out.println("Введите число: ");
                transactionManager.getScanner().nextLine();
            }
        }
    }
    private void wholePeriod(){
        double totalIncome = 0;
        double totalExpense = 0;
        for (Transaction t : currentUser.getTransactions()) {
            if (t.getType()== TransactionType.INCOME) {
                totalIncome += t.getAmount();
            }
            if (t.getType()== TransactionType.EXPENSE) {
                totalExpense += t.getAmount();
            }
        }
        double netResult = totalIncome - totalExpense;
        System.out.println("Статистика за весь период: \n" +
                "Доходы: " + totalIncome + "\n" +
                "Расходы: " + totalExpense + "\n" +
                "Чистый результат: " + netResult);
        if(netResult<0){
            System.out.println("Дефицит"+Math.abs(netResult));
        }
    }
    private void currentPeriod() {
        boolean flag = true;
        LocalDate start = null;
        LocalDate end = null;
        while (flag) {
            System.out.println("Начало (yyyy-MM-dd):");
            start=transactionManager.getValidDate();
            System.out.println("Конец (yyyy-MM-dd):");
            end=transactionManager.getValidDate();
            if (!start.isAfter(end)) {
                flag = false;
            }else
                System.out.println("Начало позже конца");
        }
        double totalIncome = 0;
        double totalExpense = 0;
        for (Transaction t : currentUser.getTransactions()) {
            if(!t.getDate().isBefore(start) && !t.getDate().isAfter(end)){
                if (t.getType() == TransactionType.INCOME){
                    totalIncome += t.getAmount();
                }
                if (t.getType() == TransactionType.EXPENSE) {
                    totalExpense += t.getAmount();
                }
            }
        }
        double netResult = totalIncome - totalExpense;
        System.out.println("Статистика за период "+start+" - "+end+": \n" +
                "Доходы: " + totalIncome + "\n" +
                "Расходы: " + totalExpense + "\n" +
                "Чистый результат: " + netResult);
        if(netResult<0){
            System.out.println("Дефицит"+Math.abs(netResult));
        }


    }
    private void categoryStatistics() {
        if (currentUser.getTransactions().isEmpty()){
            System.out.println("Транзакций нет");
            return;
        }
        boolean flagDate = true;
        LocalDate start = null;
        LocalDate end = null;
        while (flagDate) {
            System.out.println("Начало (yyyy-MM-dd):");
            start=transactionManager.getValidDate();
            System.out.println("Конец (yyyy-MM-dd):");
            end=transactionManager.getValidDate();
            if (!start.isAfter(end)) {
                flagDate = false;
            }else
                System.out.println("Начало позже конца");
        }
        Map<String,Double> categoryStatistics = new HashMap<>();
        double totalExpenses = 0;
        for (Transaction t : currentUser.getTransactions()) {
            if (!t.getDate().isBefore(start) && !t.getDate().isAfter(end)) {
                if(t.getType() == TransactionType.EXPENSE){
                    String category = t.getCategory();
                    double currentAmount = categoryStatistics.getOrDefault(category, 0.0);
                    categoryStatistics.put(category, currentAmount + t.getAmount());
                    totalExpenses += t.getAmount();
                }
            }
            System.out.println("Анализ расходов по категориям за "+start+" - "+end+":");
            if(categoryStatistics.isEmpty()){
                System.out.println("Расходов за период нет");
            }else {
                for (Map.Entry<String, Double> entry : categoryStatistics.entrySet()) {
                    String key = entry.getKey();
                    Double value = entry.getValue();
                    System.out.println("Категория: " + key + " | Расходы: " + value);
                    totalExpenses += value;
                }
            }
            System.out.println("Общий расход: " + totalExpenses);
        }

    }

}
