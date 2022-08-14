package com.example.jwtprac.dto;


import lombok.Data;

@Data
public class LoginRequestDto {
    private String username;
    private String password;
}
