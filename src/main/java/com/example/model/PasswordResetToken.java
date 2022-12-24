package com.example.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "PassowordResetTokens")
public class PasswordResetToken extends Token {
    private static final int PASSWORD_RESET_EXPIRATION = 60;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public PasswordResetToken(){}

    public PasswordResetToken(User user) {
        super(user);
    }

    @Override
    public String generateToken() {
        return super.generateToken();
    }

    @Override
    public Date calculateExpiryDate(int expiryTimeInMinutes) {
        return super.calculateExpiryDate(PASSWORD_RESET_EXPIRATION);
    }
}
