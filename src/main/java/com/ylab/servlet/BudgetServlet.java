package com.ylab.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylab.dto.BudgetDTO;
import com.ylab.entity.User;
import com.ylab.mapper.BudgetMapper;
import com.ylab.repository.impl.JdbcBudgetRepository;
import com.ylab.repository.impl.JdbcTransactionRepository;
import com.ylab.service.BudgetService;
import com.ylab.service.TransactionService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/budgets/*")
public class BudgetServlet extends BaseServlet {
    private final BudgetService budgetService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final BudgetMapper budgetMapper = Mappers.getMapper(BudgetMapper.class);

    public BudgetServlet() throws SQLException {
        this.budgetService = new BudgetService(
                new JdbcBudgetRepository(getDbProperties()),
                new TransactionService(new JdbcTransactionRepository(getDbProperties()))
        );
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            BudgetDTO budgetDTO = objectMapper.readValue(req.getInputStream(), BudgetDTO.class);
            User admin = new User("admin@example.com", "admin", "password", true);
            User targetUser = new User(budgetDTO.getEmail(), "", "", false);
            budgetService.setMonthlyBudget(
                    admin,
                    targetUser,
                    budgetDTO.getLimit(),
                    budgetDTO.getStart().getYear(),
                    budgetDTO.getStart().getMonthValue()
            );
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(budgetDTO));
        } catch (IllegalArgumentException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            User admin = new User("admin@example.com", "admin", "password", true);
            User targetUser = admin;
            List<BudgetDTO> budgets = null;
            try {
                budgets = budgetService.getUserBudgets(admin, targetUser).stream()
                        .map(budgetMapper::toDto)
                        .collect(Collectors.toList());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            resp.getWriter().write(objectMapper.writeValueAsString(budgets));
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            BudgetDTO budget = null;
            try {
                budget = budgetMapper.toDto(budgetService.findById(id));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (budget != null) {
                resp.getWriter().write(objectMapper.writeValueAsString(budget));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Бюджет не найден\"}");
            }
        }
    }
}