package com.example.jwtprac.dto;

import lombok.Data;

@Data
public class SignupRequestDto {
    private String username;
    private String nickname;
    private String password;
    private String passwordCheck;
    private String oauth;
    private String UserProfile;
}