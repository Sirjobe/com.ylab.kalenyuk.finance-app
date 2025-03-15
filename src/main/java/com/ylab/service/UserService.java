package com.ylab.service;

import com.ylab.entity.User;
import com.ylab.repository.UserRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import java.util.Properties;

/**
 * Сервис для управления бизнес-логикой пользователей.
 */

public class UserService {
    private final UserRepository userRepository;
    private Properties adminProperties;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
        adminProperties = new Properties();
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("admin.properties")) {
            if (input == null) {
                throw new FileNotFoundException("Файл admin.properties не найден!");
            }
            adminProperties.load(input);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Не удалось загрузить файл конфигурации", e);
        }
    }

    /**
     * Регистрирует нового пользователя.
     *
     * @param email email пользователя
     * @param username имя пользователя
     * @param password пароль
     * @throws IllegalArgumentException если email уже занят или данные некорректны
     */
    public  void registration(String email, String username, String password) {
        User user;
        String adminEmails = adminProperties.getProperty("admin.emails");
        boolean isAdmin = adminEmails != null && adminEmails.contains(email);
        if(userRepository.findByEmail(email) !=null ){
            throw  new IllegalArgumentException("Пользователь с таким email уже существует!");
        }
        if (isAdmin) {
             user = new User(email, username, password,true);
            userRepository.save(user);
        }

        user = new User(email, username, password,false);

        if(!user.isPasswordValid()){
            throw new IllegalArgumentException("Пароль должен быть длиннее 6 символов");
        }
        userRepository.save(user);



    }

    /**
     * Редактирует данные пользователя.
     *
     * @param email email пользователя
     * @param newEmail новый email (не может быть null)
     * @param newUsername новое имя (может быть null, если не меняется)
     * @param newPassword новый пароль (может быть null, если не меняется)
     * @throws IllegalArgumentException если пользователь не найден
     */
    public void editUser(String email, String newEmail, String newUsername, String newPassword) {
        User user = userRepository.findByEmail(email);
        if(user == null){
           throw  new IllegalArgumentException("Пользователь не найден");
        }
        if(newUsername != null && !newUsername.isEmpty()){
            user.setUsername(newUsername);
        }
        if(newPassword != null && !newPassword.isEmpty()){
            if(!new User(email, user.getUsername(), newPassword ,false).isPasswordValid()){
                throw new IllegalArgumentException("Новый пароль слишком короткий");
            }
            user.setPassword(newPassword);
        }if (newEmail != null && !newEmail.equals(email)) {
            if (!new User(newEmail, "temp", "temp", false).getValidEmail(newEmail)) {
                throw new IllegalArgumentException("Некорректный формат нового email");
            }
            if (userRepository.findByEmail(newEmail) != null) {
                throw new IllegalArgumentException("Новый email уже занят");
            }
            user = new User(newEmail, user.getUsername(), user.getPassword(), false);
        }
        userRepository.save(user);
    }

    /**
     * Выполняет вход пользователя.
     *
     * @param email email пользователя
     * @param password пароль
     * @return пользователь, если аутентификация успешна
     * @throws IllegalArgumentException если данные неверны
     */
    public User login(String email, String password) {
        User user = userRepository.findByEmail(email);
        if(user == null|| !user.getPassword().equals(password) ){
            throw new IllegalArgumentException("Неверный email или пароль");
        }else if(user.isBlocked()){
            throw new IllegalArgumentException ("Пользователь заблокирован");
        }
        return user;
    }

    /**
     * Удаляет пользователя по email.
     *
     * @param email email пользователя для удаления
     * @param username текущий пользователь (для проверки прав)
     * @throws IllegalArgumentException если удаление невозможно
     */
    public void deleteUser(String email, User username) {
        User userToDelete = userRepository.findByEmail(email);
        if(userToDelete == null){
            throw new IllegalArgumentException("Пользователь не найден");
        }
        if(!username.isAdmin() || !userToDelete.getUsername().equals(username.getUsername())){
            throw new IllegalArgumentException("Только администратор может удалять пользователей");
        }
        userRepository.deleteByEmail(email);
    }

    /**
     * Возвращает список всех пользователей (только для администраторов).
     */
    public List<User> getAllUsers(User admin) throws SQLException {
        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("Только администраторы могут просматривать список пользователей");
        }
        return userRepository.findAll();
    }

    /**
     * Блокирует пользователя по email (только для администраторов).
     */
    public boolean blockUser(User admin, String userEmail) {
        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("Только администраторы могут блокировать пользователей");
        }
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        if(user.isBlocked()){
            user.setBlocked(false);
            userRepository.save(user);
            return false;
        }else{
            user.setBlocked(true);
            userRepository.save(user);
            return true;
        }
    }

    /**
     * Удаляет пользователя по email (только для администраторов).
     */
    public void deleteUser(User admin, String userEmail) {
        if (!admin.isAdmin()) {
            throw new IllegalArgumentException("Только администраторы могут удалять пользователей");
        }
        User user = userRepository.findByEmail(userEmail);
        if (user == null) {
            throw new IllegalArgumentException("Пользователь не найден");
        }
        userRepository.deleteByEmail(userEmail);
    }



}
