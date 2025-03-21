package com.ylab.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ylab.dto.UserDTO;
import com.ylab.entity.User;
import com.ylab.mapper.UserMapper;
import com.ylab.repository.impl.JdbcUserRepository;
import com.ylab.service.UserService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.mapstruct.factory.Mappers;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/api/users/*")
public class UserServlet extends BaseServlet {
    private final UserService userService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final UserMapper userMapper = Mappers.getMapper(UserMapper.class);

    public UserServlet() throws SQLException {
        this.userService = new UserService(new JdbcUserRepository(getDbProperties()));
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setContentType("application/json");
        try {
            UserDTO userDTO = objectMapper.readValue(req.getInputStream(), UserDTO.class);
            userService.registration(userDTO.getEmail(), userDTO.getUsername(), userDTO.getPassword());
            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(userDTO));
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
            try {
                User admin = getCurrentAdmin(req);
                List<UserDTO> users = userService.getAllUsers(admin).stream()
                        .map(userMapper::toDto)
                        .collect(Collectors.toList());
                resp.getWriter().write(objectMapper.writeValueAsString(users));
            } catch (IllegalStateException | SQLException e) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\": \"" + e.getMessage() + "\"}");
            }
        } else {
            String email = pathInfo.substring(1);
            UserDTO user = userMapper.toDto(userService.findByEmail(email));
            if (user != null) {
                resp.getWriter().write(objectMapper.writeValueAsString(user));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\": \"Пользователь не найден\"}");
            }
        }
    }

    private User getCurrentAdmin(HttpServletRequest req) {

        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("currentUser") == null) {
            throw new IllegalStateException("Пользователь не авторизован");
        }

        User currentUser = (User) session.getAttribute("currentUser");

        if (!currentUser.isAdmin()) {
            throw new IllegalStateException("Доступ запрещен: требуется администратор");
        }

        return currentUser;
    }

}