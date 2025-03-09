package com.ylab.entity;


import java.util.ArrayList;
import java.util.List;

public class User {
  private final String email;
  private String password;
  private String username;
  private List<Transaction> transactions = new ArrayList<>();
  private List<Goal> goals;
  private List<Budget> budgets = new ArrayList<>();
  private boolean isBlocked = false;
  private boolean isAdmin = false; // По умолчанию не администратор

    public boolean isAdmin() {
        return isAdmin;
    }

    public void setAdmin(boolean admin) {
        isAdmin = admin;
    }
    public void setBudgets(List<Budget> budgets) {
        this.budgets = budgets;
    }

    public List<Budget> getBudgets() {
        return budgets;
    }

    public User(String email, String username, String password) {
      this.email = email;
      this.username = username;
      this.password = password;

  }
    public boolean isBlocked() {
        return isBlocked;
    }

    public void setBlocked(boolean blocked) {
        this.isBlocked = blocked;
    }

    public List<Goal> getGoals() {
        return goals;
    }

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
  public List<Transaction> getTransactions() {
      return transactions;
  }
  public void addTransaction(Transaction transaction) {
      if (transactions == null) {
          transactions = new ArrayList<Transaction>();
      }
      transactions.add(transaction);
  }



}

