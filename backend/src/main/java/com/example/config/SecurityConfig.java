package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                // 공개 API - 인증 불필요
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/rss/**").permitAll()
                .requestMatchers("/api/quizzes/**").permitAll()
                // 보호된 API - 인증 필요
                //.requestMatchers("/api/users/**").authenticated()
                //.requestMatchers("/api/history/**").authenticated()
                .anyRequest().permitAll();
        
        return http.build();
    }
} 