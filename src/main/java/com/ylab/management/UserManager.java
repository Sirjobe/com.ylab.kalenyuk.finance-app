package com.ylab.management;

import com.ylab.entity.User;

import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class UserManager {
    private  Map<String, User> users = new HashMap<>();
    private  User currentUser; // текущий авторизованный пользователь
    private  Scanner console = new Scanner(System.in);


    public User getCurrentUser() {
        return currentUser;
    }
    public Map<String, User> getUsers() {
        return users;
    }


    public  void registration() {
        System.out.println("Введите email: ");
        String email = console.nextLine();
        if (email.isEmpty()){
            System.out.print("Email не может быть пустым");
            return;
        } else if (users.containsKey(email)){
            System.out.print("Такой Email уже существует: "+email);
            return; // возврат в меню
        }
        System.out.println("Введите Имя: ");
        String username = console.nextLine();
        if(username.isEmpty()) {
            System.out.print("Имя не может быть пустым");
            return;
        }
        System.out.print("Введите пароль: ");
        String password = console.nextLine();
        if (password.isEmpty()) {
            System.out.print("Пароль не может быть пустым");
            return;
        }
        User newUser = new User(email, username, password);
        users.put(email, newUser);
        currentUser = newUser;
        System.out.println("Регистрация успешно завершена");
    }
    public void authentication() {
        System.out.println("Введите email: ");
        String email = console.nextLine();
        if (email.isEmpty()) {
            System.out.print("Email не может быть пустым.");
            return;
        }else if (!users.containsKey(email)){
            System.out.print("Такого Email не существует.");
            return;
        }
        User newUser = users.get(email);
        System.out.println("Введите пароль: ");
        String password = console.nextLine();
        if (password.isEmpty()) {
            System.out.print("Пароль не может быть пустым");
            return;
        }else if(!password.equals(newUser.getPassword())){
            System.out.print("Пароль неверный");
            return;
        }
        currentUser = users.get(email);
        System.out.println("Вход выполнен успешно");
    }
    public void editNameUser (String email) {
        System.out.println("Введите новое имя пользователя: ");
        String name = console.nextLine();
        if (name.isEmpty()){
            System.out.print("Имя пользователя не может быть пустым.");
        }else if(users.get(email).getUsername().equals(name)){
            System.out.print("Имя пользователя совпадает с текущим");
        }else {
            User user = users.get(email);
            user.setUsername(name);
            System.out.println("Имя изменено");
        }
    }
    public  void editPasswordUser (String email) {
        System.out.println("Введите новый пароль: ");
        String password = console.nextLine();
        if (password.isEmpty()){
            System.out.print("Пароль должен содержать символы.");
        }else if(users.get(email).getPassword().equals(password)){
            System.out.print("Пароль совпадает с текущим");
        }else {
            User user = users.get(email);
            user.setPassword(password);
            System.out.println("Пароль изменен");
        }
    }
    public void removeUser(String email) {
        System.out.println("Подтвердите удаление профиля " + users.get(email).getUsername() + " нажатием: \"y/n\"");
        String accept = console.nextLine();
        if (accept.equalsIgnoreCase("y")) {
            User removedUser = users.remove(email);
            if (currentUser != null && currentUser.equals(removedUser)) {
                currentUser = null; // Сбрасываем текущего пользователя, если он удален
            }
            System.out.println("Профиль " + removedUser.getUsername() + " удален");
        } else {
            System.out.println("Профиль не удален");
        }
    }
}
