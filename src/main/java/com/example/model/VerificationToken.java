package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "verification_tokens")
public class VerificationToken extends Token {
    private final int EXPIRATION = 60 * 24;

    public VerificationToken(){}
    public VerificationToken(User user) {
        super(user);
    }

}
