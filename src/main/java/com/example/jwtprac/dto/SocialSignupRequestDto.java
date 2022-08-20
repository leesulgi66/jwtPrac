package com.example.jwtprac.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class SocialSignupRequestDto {
    private String username;
    private String nickname;
    private String socialNickname;
    private String password;
    private String userProfile;
    private MultipartFile userProfileImage;
    private String age;
    private String gender;
    private String address;
}