package com.ylab.repository.impl;

import com.ylab.entity.Budget;
import com.ylab.entity.User;
import com.ylab.repository.BudgetRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcBudgetRepository implements BudgetRepository {
    private final Connection connection;

    public JdbcBudgetRepository(Properties dbProperties) throws SQLException {
        String url = dbProperties.getProperty("db.url");
        String username = dbProperties.getProperty("db.username");
        String password = dbProperties.getProperty("db.password");
        this.connection = DriverManager.getConnection(url, username, password);
    }

    @Override
    public void save(Budget budget) throws SQLException {
        String sql = "INSERT INTO finance.budgets (limit_amount, start_date, end_date, email) " +
                "VALUES (?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, budget.getLimit());
            stmt.setDate(2, Date.valueOf(budget.getStart()));
            stmt.setDate(3, Date.valueOf(budget.getEnd()));
            stmt.setString(4, budget.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                budget.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public Budget findById(int id) throws SQLException {
        String sql = "SELECT * FROM finance.budgets WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToBudget(rs);
            }
            return null;
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
            throw e;
        }
    }

    @Override
    public List<Budget> findByUser(User user) throws SQLException {
        String sql = "SELECT * FROM finance.budgets WHERE email = ?";
        List<Budget> budgets = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                budgets.add(mapRowToBudget(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
            throw e;
        }
        return budgets;
    }

    @Override
    public void deleteById(int id) throws SQLException {
        String sql = "DELETE FROM finance.budgets WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
            throw e;
        }
    }

    private Budget mapRowToBudget(ResultSet rs) throws SQLException {
        Budget budget = new Budget();
        budget.setId(rs.getInt("id"));
        budget.setLimit(rs.getDouble("limit_amount"));
        budget.setStart(rs.getDate("start_date").toLocalDate());
        budget.setEnd(rs.getDate("end_date").toLocalDate());
        budget.setEmail(rs.getString("email"));
        return budget;
    }
}