package com.example.repository;

import com.example.model.User;
import com.example.model.VerificationToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VerificationTokenRepository extends JpaRepository<VerificationToken, Long> {

    VerificationToken findVerificationTokenByUser(User user);
    Long deleteVerificationTokenById(Long id);

}
