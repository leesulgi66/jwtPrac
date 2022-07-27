package com.example.jwtprac.repository;

import com.example.jwtprac.model.User;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<User, Long>{
    User findByUsername(String username);
}