package com.example.jwtprac.repository;

import com.example.jwtprac.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<Member, Long>{
    Optional<Member> findByUsername(String username);
    Optional<String> findByNickname(String nickname);
}