package com.example.jwtprac.config;

import com.example.jwtprac.config.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.header.writers.frameoptions.XFrameOptionsHeaderWriter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CorsFilter corsFilter;

    @Bean
    @Override // Bean 에 등록
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    // 정적 자원에 대해서는 Security 설정을 적용하지 않음.
    @Override
    public void configure(WebSecurity web) {
        web.ignoring().requestMatchers(PathRequest.toStaticResources().atCommonLocations());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
//        http.addFilterBefore(new MyFilter3(), SecurityContextPersistenceFilter.class);  //시큐리티 보다 이전에 필터가 작동하게 설계
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                //STATELESS -> 세션을 사용하지 않겠다는 것
        .and()
                .addFilter(corsFilter) //@CrossOrigin(인증X), 시큐리티 필터에 등록 인증(O)
                .formLogin().disable() //폼로그인 사용을 하지 않겠다는 것.
                .httpBasic().disable() //기본방식 사용하지 않겠다는 것.
                .addFilter(new JwtAuthenticationFilter(authenticationManager())) //AuthenticationManager 넘겨야 하는 파라미터!

                .authorizeRequests()
                .antMatchers("/api/v1/user/**")
                .access("hasRol('ROLE_USER')or hasRol('ROLE_MANAGER') or hasRol('ROLE_ADMIN')")
                .antMatchers("/api/v1/manager/**")
                .access("hasRol('ROLE_MANAGER') or hasRol('ROLE_ADMIN')")
                .antMatchers("/api/v1/admin/**")
                .access("hasRol('ROLE_ADMIN')")
                .antMatchers("h2-console/**").permitAll()
                .anyRequest().permitAll()
                .and().headers().addHeaderWriter(new XFrameOptionsHeaderWriter(XFrameOptionsHeaderWriter.XFrameOptionsMode.SAMEORIGIN));




    }


}
