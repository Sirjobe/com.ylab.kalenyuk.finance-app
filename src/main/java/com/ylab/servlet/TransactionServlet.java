package com.ylab.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylab.dto.TransactionDTO;
import com.ylab.entity.User;
import com.ylab.mapper.TransactionMapper;
import com.ylab.repository.impl.JdbcTransactionRepository;
import com.ylab.service.TransactionService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/transactions/*")
public class TransactionServlet extends BaseServlet {
    private final TransactionService transactionService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final TransactionMapper transactionMapper = Mappers.getMapper(TransactionMapper.class);

    public TransactionServlet() throws SQLException {
        this.transactionService = new TransactionService(new JdbcTransactionRepository(getDbProperties()));
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            TransactionDTO transactionDTO = objectMapper.readValue(req.getInputStream(), TransactionDTO.class);
            transactionService.createTransaction(
                    transactionDTO.getAmount(),
                    transactionDTO.getDescription(),
                    transactionDTO.getCategory(),
                    transactionDTO.getDate(),
                    transactionDTO.getType(),
                    transactionDTO.getEmail()
            );
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(transactionDTO));
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

            User admin = new User("admin@example.com", "admin", "password", true);
            User targetUser = admin; // В реальном приложении это будет из сессии
            List<TransactionDTO> transactions = transactionService.getUserTransaction(admin, targetUser, null, null, null, null).stream()
                    .map(transactionMapper::toDto)
                    .collect(Collectors.toList());
            resp.getWriter().write(objectMapper.writeValueAsString(transactions));
        } else {
            int id = Integer.parseInt(pathInfo.substring(1));
            TransactionDTO transaction = null;
            try {
                transaction = transactionMapper.toDto(transactionService.findById(id));
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
            if (transaction != null) {
                resp.getWriter().write(objectMapper.writeValueAsString(transaction));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Транзакция не найдена\"}");
            }
        }
    }
}