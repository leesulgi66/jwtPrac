package com.example.jwtprac.config;

import com.example.jwtprac.config.jwt.JwtAuthenticationFilter;
import com.example.jwtprac.filter.MyFilter3;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.context.SecurityContextPersistenceFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);  //시큐리티 보다 이전에 필터가 작동하게 설계
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //STATELESS -> 세션을 사용하지 않겠다는 것
        .and()
        .addFilter(corsFilter) //@CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O)
        .formLogin().disable() //폼로그인 사용을 하지 않겠다는 것.
        .httpBasic().disable() //기본방식 사용하지 않겠다는 것.

        .addFilter(new JwtAuthenticationFilter())
        .authorizeRequests()
        .antMatchers("/api/v1/user/**")
        .access("hasRol('ROLE_USER')or hasRol('ROLE_MANAGER') or hasRol('ROLE_ADMIN')")
        .antMatchers("/api/v1/manager/**")
        .access("hasRol('ROLE_MANAGER') or hasRol('ROLE_ADMIN')")
        .antMatchers("/api/v1/admin/**")
        .access("hasRol('ROLE_ADMIN')")
        .anyRequest().permitAll();
    }
}
