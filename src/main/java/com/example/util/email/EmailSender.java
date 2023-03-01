package com.example.util.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EmailSender implements EmailService {
    private Environment env;
    private final JavaMailSender mailSender;

    @Override
    @Async
    public void send(String to, String subject, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(content, true);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setFrom("Hacettepe Üniversitesi Öğrenci İşleri Daire Başkanlığı");
            mailSender.send(mimeMessage);
            System.out.println("Yollandı...");
        } catch (MessagingException e ){
            System.out.println(e.getMessage());
            throw new IllegalStateException("Failed to send email.");
        }
    }
}
