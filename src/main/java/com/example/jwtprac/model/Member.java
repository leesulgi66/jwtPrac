package com.example.jwtprac.model;

import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@Entity
public class Member extends Timestamped {
    @Id  //ID 할당 방법 1.직접 넣는 방식 (Setter, 생성자) 2.(JPA나)DB에게 할당 책임을 전가. (@GenerateValue)
    @GeneratedValue(strategy = GenerationType.IDENTITY) // mysql 은 identity. auto는 안 맞을 경우도 있어.
    private Long id;

    @Column(nullable = false)
    private String username;
    @Column//(nullable = false)
    private String password;
    @Column
    private String nickname;
    @Column(length = 1000)
    private String profileImage;
    @Column
    private String oauth;
    @Column
    private String socialNickname;
    @Column
    private String age;
    @Column
    private String gender;
    @Column
    private String address;

    private String role;

    public Member(String username, String password, String profileImage, String oauth, String socialNickname) {
        this.username = username;
        this.password = password;
        this.profileImage = profileImage;
        this.oauth = oauth;
        this.socialNickname = socialNickname;
    }

    public Member(String username, String password, String profileImage, String nickname) {
        this.username = username;
        this.password = password;
        this.profileImage = profileImage;
        this.nickname = nickname;
    }

    public Member(String username, String password, String profileImage, String nickname, String age, String gender, String address, String role) {
        this.username = username;
        this.password = password;
        this.profileImage = profileImage;
        this.nickname = nickname;
        this.age = age;
        this.gender = gender;
        this.address = address;
        this.role = role;
    }
}
