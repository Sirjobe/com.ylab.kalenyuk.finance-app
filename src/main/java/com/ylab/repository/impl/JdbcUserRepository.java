package com.ylab.repository.impl;

import com.ylab.entity.User;
import com.ylab.repository.UserRepository;
import java.sql.SQLException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcUserRepository implements UserRepository {
    private final Connection connection;

    public JdbcUserRepository(Properties dbProperties) throws SQLException {
        String url = dbProperties.getProperty("db.url");
        String username = dbProperties.getProperty("db.username");
        String password = dbProperties.getProperty("db.password");
        this.connection = DriverManager.getConnection(url, username, password);
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO finance.users (email, username, password, is_admin, is_blocked) " +
                "VALUES (?, ?, ?, ?, ?) ON CONFLICT (email) DO UPDATE " +
                "SET username = EXCLUDED.username, password = EXCLUDED.password, is_admin = EXCLUDED.is_admin, is_blocked = EXCLUDED.is_blocked " +
                "RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getUsername());
            stmt.setString(3, user.getPassword());
            stmt.setBoolean(4, user.isAdmin());
            stmt.setBoolean(5, user.isBlocked());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                user.setId(rs.getInt("id"));
            }
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public User findByEmail(String email) {
        String sql = "SELECT * FROM finance.users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToUser(rs);
            }
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return null;
    }

    @Override
    public void deleteByEmail(String email) {
        String sql = "DELETE FROM finance.users WHERE email = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, email);
            stmt.executeUpdate();
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public List<User> findAll() {
        String sql = "SELECT * FROM finance.users";
        List<User> users = null;
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            users = new ArrayList<>();
            while (rs.next()) {
                users.add(mapRowToUser(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return users;
    }

    private User mapRowToUser(ResultSet rs) {
        User user = null;
        try {
            user = new User(rs.getString("email"), rs.getString("username"), rs.getString("password"), rs.getBoolean("is_admin"));
            user.setId(rs.getInt("id"));
            user.setBlocked(rs.getBoolean("is_blocked"));
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return user;
    }

}