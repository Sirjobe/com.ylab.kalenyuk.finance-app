package com.ylab.entity;


import java.util.regex.Pattern;

/**
 * Класс, представляющий пользователя приложения.
 */

public class User {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private final String email;
    private int id;
    private String password;
    private String username;
    private boolean blocked;
    private boolean isAdmin;

    /**
     * Создает нового пользователя.
     *
     * @param email адрес электронной почты пользователя
     * @param username имя пользователя
     * @param password пароль пользователя
     */
     public User(String email, String username, String password, boolean isAdmin) {
      if (!isValidEmail(email)) {
          throw new IllegalArgumentException("Некорректный формат email");
      }
      this.email = email;
      this.username = username;
      this.password = password;
      this.isAdmin = isAdmin;
      this.blocked = false;

    }

     public User(){
         this.email = null;
     }

     public int getId() {
         return id;
     }

     public void setId(int id) {
         this.id = id;
     }

     public boolean isAdmin() {
        return isAdmin;
 }

     public boolean isBlocked() {
        return blocked;
    }

     public void setBlocked(boolean blocked) { this.blocked = blocked; }

     public String getUsername() {
        return username;
  }

     public String getEmail() {
      return email;
  }

     public String getPassword() {
      return password;
  }

     public void setUsername(String username) {
      this.username = username;
  }

    public void setPassword(String password) {
      this.password = password;
  }

     public boolean isPasswordValid() {
        return password != null && password.length() >= 6;
  }

    private boolean isValidEmail(String email) {
      if (email == null || email.isEmpty()) return false;
      return EMAIL_PATTERN.matcher(email).matches();
  }

    public boolean getValidEmail(String email) {
         return isValidEmail(email);
    }


}

