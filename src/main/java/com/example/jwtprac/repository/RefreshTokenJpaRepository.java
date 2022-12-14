package com.example.jwtprac.repository;

import com.example.jwtprac.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenJpaRepository extends JpaRepository<RefreshToken, String> {
    Optional<RefreshToken> findByTokenKey(Long Key);
    void deleteAllByTokenKey(Long key);
}
