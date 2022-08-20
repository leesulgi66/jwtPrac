package com.example.jwtprac.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SignupRequestDto {
    private String username;
    private String nickname;
    private String socialNickname;
    private String password;
    private String passwordCheck;
    private String oauth;
    private String userProfile;
    private MultipartFile userProfileimage;
    private String age;
    private String gender;
    private String address;
}