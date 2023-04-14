package com.example.repository;

import com.example.model.User;
import com.example.model.VerificationToken;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationTokenRepository extends TokenRepository<VerificationToken> {
    VerificationToken findVerificationTokenByUser(User user);
    Long deleteVerificationTokenById(Long id);
}
