package com.ylab.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylab.dto.GoalDTO;
import com.ylab.mapper.GoalMapper;
import com.ylab.repository.impl.JdbcGoalRepository;
import com.ylab.repository.impl.JdbcTransactionRepository;
import com.ylab.service.GoalService;
import com.ylab.service.StatisticsService;
import com.ylab.service.TransactionService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/goals/*")
public class GoalServlet extends HttpServlet {
    private final GoalService goalService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final GoalMapper goalMapper = Mappers.getMapper(GoalMapper.class);

    public GoalServlet() throws SQLException {
        this.goalService = new GoalService(
                new JdbcGoalRepository(getDbProperties()),
                new StatisticsService(new TransactionService(new JdbcTransactionRepository(getDbProperties())))
        );
    }

    private java.util.Properties getDbProperties() {
        java.util.Properties props = new java.util.Properties();
        try (java.io.FileInputStream fis = new java.io.FileInputStream("src/main/resources/application.properties")) {
            props.load(fis);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить свойства базы данных", e);
        }
        return props;
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            GoalDTO goalDTO = objectMapper.readValue(req.getInputStream(), GoalDTO.class);
            com.ylab.entity.User user = new com.ylab.entity.User(goalDTO.getEmail(), "", "", false);
            goalService.setGoal(
                    user,
                    goalDTO.getTargetAmount(),
                    goalDTO.getDescription(),
                    goalDTO.getEndDate()
            );
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(goalDTO));
        } catch (IllegalArgumentException | SQLException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            com.ylab.entity.User user = new com.ylab.entity.User("admin@example.com", "admin", "password", true);
            List<GoalDTO> goals = null;
            try {
                goals = goalService.getUserGoals(user).stream()
                        .map(goalMapper::toDto)
                        .collect(Collectors.toList());
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            resp.getWriter().write(objectMapper.writeValueAsString(goals));
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            GoalDTO goal = goalMapper.toDto(goalService.findById(id));
            if (goal != null) {
                resp.getWriter().write(objectMapper.writeValueAsString(goal));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Цель не найдена\"}");
            }
        }
    }
}