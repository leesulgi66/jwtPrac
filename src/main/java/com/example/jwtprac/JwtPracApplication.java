package com.example.jwtprac;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class JwtPracApplication {

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    public static final String APPLICATION_LOCATIONS = "spring.config.location="
            + "classpath:application-aws.yml,"
            + "classpath:application.yml";

    public static void main(String[] args) {
        new SpringApplicationBuilder(JwtPracApplication.class)
                .properties(APPLICATION_LOCATIONS)
                .run(args);
    }
}
