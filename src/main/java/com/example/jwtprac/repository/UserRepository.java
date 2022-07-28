package com.example.jwtprac.repository;

import com.example.jwtprac.model.Member;
import org.springframework.data.jpa.repository.JpaRepository;


public interface UserRepository extends JpaRepository<Member, Long>{

    Member findByUsername(String username);

}