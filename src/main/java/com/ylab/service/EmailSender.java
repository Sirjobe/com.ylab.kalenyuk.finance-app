package com.ylab.service;

/**
 * Интерфейс для отправки email.
 */
public interface EmailSender {
    void sendEmail(String to, String subject, String massage);
}
