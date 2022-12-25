package com.example.model;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends Token {
    private final int EXPIRATION = 60;

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
        return super.calculateExpiryDate(EXPIRATION);
    }
}
