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

    @Column(nullable = false, unique = true)
    private String username;
    @Column(nullable = false)
    private String password;
    @Column(nullable = false, unique = true)
    private String nickname;

    @Column
    private String mbti;

    //@ColumnDfalt("'user'")
    //private String role; Enum - admin, user, manager 역할 입력해보기

    public Member(String username, String password, String nickname) {
        this.username = username;
        this.password = password;
        this.nickname = nickname;
    }

    public Member(String username, String usernickname) {
        this.username = username;
        this.nickname = usernickname;
    }
}
