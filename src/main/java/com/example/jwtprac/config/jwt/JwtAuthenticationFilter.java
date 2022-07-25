package com.example.jwtprac.config.jwt;

import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

//스프링 시큐리티에서 UsernamePasswordAuthenticationFilter 가 있음.
// /login 요청해서 username, password 전송하면 (psot)
//UsernamePasswordAuthenticationFilter 동작을 함.
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
}
