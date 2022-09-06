package com.example.jwtprac.dto;

import lombok.*;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor

public class SocialReponseDto {
    private Long id;
    private String username;
    private String nickname;
    private String profileImage;
    private String age;
    private String address;
    private String gender;
}
