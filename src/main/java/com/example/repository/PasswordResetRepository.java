package com.example.repository;

import com.example.model.PasswordResetToken;
import com.example.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PasswordResetRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
}
