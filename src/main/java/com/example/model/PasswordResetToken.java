package com.example.model;


import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Entity
@NoArgsConstructor
@Table(name = "password_reset_tokens")
public class PasswordResetToken extends Token {
    private final int EXPIRATION = 60;
    public PasswordResetToken(User user) {
        super(user);
    }
}
