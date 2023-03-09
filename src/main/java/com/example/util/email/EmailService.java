package com.example.util.email;

public interface EmailService {
    void send(String to, String subject, String content);
}
