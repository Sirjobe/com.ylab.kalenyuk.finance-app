package com.ylab.servlet;

import jakarta.servlet.http.HttpServlet;
import java.io.InputStream;
import java.io.IOException;
import java.util.Properties;

public abstract class BaseServlet extends HttpServlet {
    protected Properties getDbProperties() {
        Properties props = new Properties();
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("application.properties")) {
            if (is == null) {
                throw new IOException("Файл application.properties не найден");
            }
            props.load(is);
        } catch (IOException e) {
            throw new RuntimeException("Не удалось загрузить свойства базы данных", e);
        }
        return props;
    }
}