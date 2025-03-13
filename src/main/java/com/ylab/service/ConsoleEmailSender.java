package com.ylab.service;

/**
 *  Реализация EmailSender для консольного вывода.
 */
public class ConsoleEmailSender implements EmailSender {
    @Override
    public void sendEmail(String to, String subject, String message) {
        System.out.println("Отправка email на " + to + ":");
        System.out.println("Тема: " + subject);
        System.out.println("Сообщение:\n" + message);
        System.out.println("----------------");
    }
}
