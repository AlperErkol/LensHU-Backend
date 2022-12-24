package com.example.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "VerificationTokens")
public class VerificationToken extends Token {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    public VerificationToken(){}
    public VerificationToken(User user) {
        super(user);
    }

}
