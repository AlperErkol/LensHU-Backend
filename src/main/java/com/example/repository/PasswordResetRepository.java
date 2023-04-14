package com.example.repository;
import com.example.model.PasswordResetToken;
import com.example.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface PasswordResetRepository extends TokenRepository<PasswordResetToken> {
    PasswordResetToken findPasswordResetTokenByUser(User user);
    Long deletePasswordResetTokenById(Long id);
}
