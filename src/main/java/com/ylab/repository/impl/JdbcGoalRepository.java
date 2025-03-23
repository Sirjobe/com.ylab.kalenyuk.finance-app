package com.ylab.repository.impl;

import com.ylab.entity.Goal;
import com.ylab.entity.User;
import com.ylab.repository.GoalRepository;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcGoalRepository implements GoalRepository {
    private final Connection connection;

    public JdbcGoalRepository(Properties dbProperties) throws SQLException {
        String url = dbProperties.getProperty("db.url");
        String username = dbProperties.getProperty("db.username");
        String password = dbProperties.getProperty("db.password");
        this.connection = DriverManager.getConnection(url, username, password);
    }

    @Override
    public void save(Goal goal) {
        String sql = "INSERT INTO finance.goals (target_amount, start_date, end_date, description, email) " +
                "VALUES (?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, goal.getTargetAmount());
            stmt.setDate(2, Date.valueOf(goal.getStartDate()));
            stmt.setDate(3, Date.valueOf(goal.getEndDate()));
            stmt.setString(4, goal.getDescription());
            stmt.setString(5, goal.getEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                goal.setId(rs.getInt("id"));
            }
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public Goal findById(int id)  {
        String sql = "SELECT * FROM finance.goals WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToGoal(rs);
            }
        }
        catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Goal> findByUser(User user) {
        String sql = "SELECT * FROM finance.goals WHERE email = ?";
        List<Goal> goals = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            ResultSet rs = stmt.executeQuery();
            goals = new ArrayList<>();
            while (rs.next()) {
                goals.add(mapRowToGoal(rs));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return goals;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM finance.goals WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    private Goal mapRowToGoal(ResultSet rs) {
        Goal goal = new Goal();
        try{
            goal.setId(rs.getInt("id"));
            goal.setTargetAmount(rs.getDouble("target_amount"));
            goal.setStartDate(rs.getDate("start_date").toLocalDate());
            goal.setEndDate(rs.getDate("end_date").toLocalDate());
            goal.setDescription(rs.getString("description"));
            goal.setEmail(rs.getString("email"));
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return goal;
    }

}