package com.ylab.repository.impl;

import com.ylab.entity.Transaction;
import com.ylab.entity.TransactionType;
import com.ylab.entity.User;
import com.ylab.repository.TransactionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class JdbcTransactionRepository implements TransactionRepository {
    private final Connection connection;

    public JdbcTransactionRepository(Properties dbProperties) throws SQLException {
        String url = dbProperties.getProperty("db.url");
        String username = dbProperties.getProperty("db.username");
        String password = dbProperties.getProperty("db.password");
        this.connection = DriverManager.getConnection(url, username, password);
    }

    @Override
    public void save(Transaction transaction) {
        String sql = "INSERT INTO finance.transactions (amount, description, category, transaction_date, transaction_type, email) " +
                "VALUES (?, ?, ?, ?, ?, ?) RETURNING id";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setDouble(1, transaction.getAmount());
            stmt.setString(2, transaction.getDescription());
            stmt.setString(3, transaction.getCategory());
            stmt.setDate(4, Date.valueOf(transaction.getDate()));
            stmt.setString(5, transaction.getType().name());
            stmt.setString(6, transaction.getUserEmail());
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                transaction.setId(rs.getInt("id"));
            }
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    @Override
    public Transaction findById(int id) {
        String sql = "SELECT * FROM finance.transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return mapRowToTransaction(rs);
            }
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return null;
    }

    @Override
    public List<Transaction> findByUser(User user) {
        String sql = "SELECT * FROM finance.transactions WHERE email = ?";
        List<Transaction> transactions = null;
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, user.getEmail());
            ResultSet rs = stmt.executeQuery();
            transactions = new ArrayList<>();
            while (rs.next()) {
                transactions.add(mapRowToTransaction(rs));
            }

        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return transactions;
    }

    @Override
    public void deleteById(int id) {
        String sql = "DELETE FROM finance.transactions WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
    }

    private Transaction mapRowToTransaction(ResultSet rs) {
        Transaction transaction = new Transaction();
        try {
            transaction.setId(rs.getInt("id"));
            transaction.setAmount(rs.getDouble("amount"));
            transaction.setDescription(rs.getString("description"));
            transaction.setCategory(rs.getString("category"));
            transaction.setDate(rs.getDate("transaction_date").toLocalDate());
            transaction.setType(TransactionType.valueOf(rs.getString("transaction_type")));
            transaction.setEmail(rs.getString("email"));
        } catch (SQLException e) {
            System.err.println("Ошибка базы данных: " + e.getMessage());
        }
        return transaction;
    }
}