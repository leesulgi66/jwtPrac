package com.example.jwtprac.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@Table(name = "REFRESH_TOKEN")
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class RefreshToken extends Timestamped{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long tokenKey;

    @Column(nullable = false)
    private  String refreshToken;

    public RefreshToken updateToken(String token) {
        this.refreshToken = token;
        return this;
    }
    @Builder
    public RefreshToken(Long key, String Token) {
        this.tokenKey = key;
        this.refreshToken = Token;
    }
}
