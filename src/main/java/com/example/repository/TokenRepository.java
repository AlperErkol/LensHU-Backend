package com.example.repository;

import com.example.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface TokenRepository<T extends Token> extends JpaRepository<T, Long> {
}
