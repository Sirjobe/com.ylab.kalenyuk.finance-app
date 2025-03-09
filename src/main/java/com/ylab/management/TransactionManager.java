package com.ylab.management;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class TransactionManager {
    private User currentUser;
    private Scanner scanner = new Scanner(System.in);
    public TransactionManager(User currentUser) {
        this.currentUser = currentUser;
    }
    public Scanner getScanner() {
        return scanner;
    }
    public void addTransaction() {
        double amount = getValidAmount();
        String description = getValidDescription();
        String category = getValidCategory();
        LocalDate date = getValidDate();
        TransactionType type = getValidType();
        Transaction transaction = new Transaction(amount, description, category, date, type);
        currentUser.addTransaction(transaction);
        System.out.println("Транзакция добавлена");
    }
    public void editTransaction() {
        checkTransaction();
        editMenu();
        System.out.println("Транзакция обновлена!");
    }
    public void removeTransaction() {
        System.out.println("Введите ID транзакции для удаления:");
        int id = scanner.nextInt();
        scanner.nextLine();
        Transaction toRemove = null;
        for (Transaction t : currentUser.getTransactions()) {
            if (t.getId() == id) {
                toRemove = t;
                break;
            }
        }
        if (toRemove != null) {
            currentUser.getTransactions().remove(toRemove);
            System.out.println("Транзакция удалена.");
        } else {
            System.out.println("Транзакция не найдена.");
        }
    }
    public void viewTransaction() {
        checkTransaction();
        List<Transaction> filteredTransactions = new ArrayList<>(currentUser.getTransactions());
        System.out.println("Выведи: \n" +
                "1. Все транзакции \n" +
                "2. По дате \n" +
                "3. По категории \n" +
                "4. По типу\n" +
                "0.Назад");
        boolean viewing = true;
        while (viewing) {
            int indexMenu = scanner.nextInt();
            scanner.nextLine();
            switch (indexMenu) {
                case 0:
                    viewing = false;
                case 1:
                    filteredTransactions = new ArrayList<>(currentUser.getTransactions());
                    viewFilter(filteredTransactions);
                    break;
                case 2:
                    filterDate(filteredTransactions);
                    viewFilter(filteredTransactions);
                    break;
                case 3:
                    filterCategory(filteredTransactions);
                    viewFilter(filteredTransactions);
                    break;
                case 4:
                    filterType(filteredTransactions);
                    viewFilter(filteredTransactions);
                    break;
                default:
                    System.out.println("Неверный выбор");
            }
        }

    }
    public double getValidAmount() {
        boolean flagAmount = true;
        double amount = 0;
        while (flagAmount) {
            try {
                System.out.println("Введите сумму: ");
                amount = scanner.nextDouble();
                scanner.nextLine(); // очистка буфера
                if (amount > 0) {
                    flagAmount = false;
                } else
                    System.out.println("Сумма должна быть положительной");
            } catch (InputMismatchException e) {
                System.out.println("Введи число");
            }
        }
        return amount;
    }
    private String getValidDescription() {
        boolean flagDescription = true;
        String description = "";
        while (flagDescription) {
            System.out.println("Введите описание:");
            description = scanner.nextLine();
            if (!description.isEmpty()) {
                flagDescription = false;
            }else
                System.out.println("Описание не может быть пустым");
        }
        return description;
    }
    private String getValidCategory() {
        boolean flagCategory = true;
        String category = "";
        while (flagCategory) {
            System.out.println("Введите категорию: ");
            category = scanner.nextLine();
            if (!category.isEmpty()) {
                flagCategory = false;
            }else
                System.out.println("Категория не может быть пустой");
        }
        return category;
    }
    public LocalDate getValidDate() {
        boolean flagDate = true;
        String date = "";
        LocalDate transactionDate = null;
        while (flagDate) {
            System.out.println("Введите дату в формате (yyyy-MM-dd): ");
            date = scanner.nextLine();
            if (!date.isEmpty()) {
                try {
                    transactionDate = LocalDate.parse(date);
                    flagDate = false;
                } catch (DateTimeException e) {
                    System.out.println("Неверный формат даты (используйте yyyy-MM-dd)");
                }
            } else
                System.out.println("Дата не может быть пустой");
        }
        return transactionDate;
    }
    private TransactionType getValidType() {
        boolean flagType = true;
        int type = 0;
        TransactionType transactionType = null;
        while (flagType) {
            try {
                System.out.println("Тип: \"1.Доход, 2 Расход:\"");
                type = scanner.nextInt();
                scanner.nextLine(); // очистка буфера
            } catch (InputMismatchException e) {
                System.out.println("Введи число");
            }
            if (type == 1) {
                transactionType = TransactionType.INCOME;
                flagType = false;
            } else if (type == 2) {
                transactionType = TransactionType.EXPENSE;
                flagType = false;
            } else
                System.out.println("Неверный тип");
        }
        return transactionType;
    }
    private int getValidIndex() {
        boolean flagIndex = true;
        int index = 0;
        while (flagIndex) {
            try {
                System.out.println("Введите номер транзакции для редактирования");
                int validIndex = scanner.nextInt();
                scanner.nextLine();
                if (validIndex >= 0 || validIndex<currentUser.getTransactions().size()) {
                    flagIndex = false;
                }else
                    System.out.println("Неверный номер транзакции");
            }catch (InputMismatchException e){
                System.out.println("Введите число");
            }
        }
        return index;
    }
    private void editMenu() {
        System.out.println("Какие данные вы хотите отредактировать: \n" +
                "1. Редактировать сумму \n" +
                "2. Редактировать описание\n" +
                "3. Редактировать категорию\n" +
                "4. Редактировать дату\n" +
                "5. Редактировать тип\n" +
                "6.Назад");
        boolean flagEdit = true;
        while (flagEdit){
            String save;
            int validIndex = getValidIndex();
            Transaction transaction = currentUser.getTransactions().get(validIndex);
            System.out.println(transaction.toString());
            switch (validIndex) {
                case 1:
                    double previousAmount = transaction.getAmount();
                    transaction.setAmount(getValidAmount());
                    System.out.println("Сохранить изменения? (y/n): ");
                    save = scanner.nextLine();
                    if (save.equalsIgnoreCase("y")) {
                        break;
                    }else if (save.equalsIgnoreCase("n")) {
                        transaction.setAmount(previousAmount);
                    }
                    break;
                case 2:
                    String prevDescription = transaction.getDescription();
                    transaction.setDescription(getValidDescription());
                    System.out.println("Сохранить изменения? (y/n): ");
                    save = scanner.nextLine();
                    if (save.equalsIgnoreCase("y")) {
                        break;
                    }else if (save.equalsIgnoreCase("n")) {
                        transaction.setDescription(prevDescription);
                    }
                    break;
                case 3:
                    String prevCategory = transaction.getCategory();
                    transaction.setCategory(getValidCategory());
                    System.out.println("Сохранить изменения? (y/n): ");
                    save = scanner.nextLine();
                    if (save.equalsIgnoreCase("y")) {
                        break;
                    }else if (save.equalsIgnoreCase("n")) {
                        transaction.setCategory(prevCategory);
                    }
                    break;
                case 4:
                    LocalDate prevDate = transaction.getDate();
                    transaction.setDate(getValidDate());
                    System.out.println("Сохранить изменения? (y/n): ");
                    save = scanner.nextLine();
                    if (save.equalsIgnoreCase("y")) {
                        break;
                    }else if (save.equalsIgnoreCase("n")) {
                        transaction.setDate(prevDate);
                    }
                    break;
                case 5:
                    TransactionType prevType = transaction.getType();
                    transaction.setType(getValidType());
                    System.out.println("Сохранить изменения? (y/n): ");
                    save = scanner.nextLine();
                    if (save.equalsIgnoreCase("y")) {
                        break;
                    }else if (save.equalsIgnoreCase("n")) {
                        transaction.setType(prevType);
                    }
                    break;
                case 6:
                    flagEdit = false;
            }
        }
    }
    private void checkTransaction() {
        if(currentUser == null) {
            System.out.println("Пользователь не авторизован");
            return;
        }else if (currentUser.getTransactions().isEmpty()){
            System.out.println("Транзакций нет");
            return;
        }
        for (Transaction transaction : currentUser.getTransactions()) {
            System.out.println(transaction.toString());
        }
    }
    private void filterDate(List<Transaction> filteredTransactions) {
        boolean flag = true;
        LocalDate startDate = null;
        LocalDate endDate = null;
        while (flag) {
            System.out.println("Введите начальную дату (yyyy-MM-dd):");
            startDate= getValidDate();
            endDate = getValidDate();
            if (startDate.isAfter(endDate)) {
                System.out.println("Начальная дата не может быть позже конечной");
            }else
                flag = false;
        }
        List<Transaction> temp = new ArrayList<>();
        for (Transaction t : filteredTransactions){
            if(t.getDate().isAfter(startDate)&&t.getDate().isBefore(endDate)|| t.getDate().equals(startDate) || t.getDate().equals(endDate)){
               temp.add(t);
            }
        }
        filteredTransactions = temp;
    }
    private void filterCategory(List<Transaction> filteredTransactions) {
        String category = getValidCategory();
        List<Transaction> temp = new ArrayList<>();
        for (Transaction t : filteredTransactions){
            if(t.getCategory().equalsIgnoreCase(category)){
                temp.add(t);
            }
        }
        filteredTransactions = temp;
    }
    private void filterType(List<Transaction> filteredTransactions) {
        TransactionType type = getValidType();
        List<Transaction> temp = new ArrayList<>();
        for (Transaction t : filteredTransactions){
            if(t.getType().equals(type)){
                temp.add(t);
            }
        }
        filteredTransactions = temp;
    }
    private void viewFilter(List<Transaction> filteredTransactions) {
        if (filteredTransactions.isEmpty()) {
            System.out.println("Транзакций по заданным критериям нет");
        }
        System.out.println("Найдено: " + filteredTransactions.size());
        for (Transaction t : filteredTransactions) {
            System.out.println(t.toString());
        }
    }

}
