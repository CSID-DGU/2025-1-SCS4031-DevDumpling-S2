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
            .csrf().disable()  // React Native 앱이므로 CSRF 보호 불필요
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
            .authorizeHttpRequests()
                // 공개 API - 인증 불필요
                .requestMatchers(
                    "/",
                    "/api/auth/**",        // 카카오 로그인 등 인증 관련
                    "/actuator/health",    // 헬스 체크
                    "/api/users/*/dummy-data",  // 더미 데이터 생성
                    "/rss/**",
                    "/api/quizzes/**",
                    "/api/fss/**"
                ).permitAll()
                // 보호된 API - 인증 필요
                .requestMatchers("/api/users/**").authenticated()
                .requestMatchers("/api/bank/**").authenticated()
                .requestMatchers("/api/card/**").authenticated()
                .requestMatchers("/api/investment/**").authenticated()
                // 나머지 요청은 인증 필요
                .anyRequest().authenticated();
        
        return http.build();
    }
} 