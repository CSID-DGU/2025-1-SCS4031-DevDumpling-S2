package com.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import com.example.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/dummy/**").permitAll()
                .requestMatchers("/api/fss/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                // 게시판 API 권한 설정
                .requestMatchers("/api/boards/*/create").authenticated()
                .requestMatchers("/api/boards/*/update/*").authenticated()
                .requestMatchers("/api/boards/*/delete/*").authenticated()
                .requestMatchers("/api/boards/**").permitAll()
                .requestMatchers("/rss/**").permitAll()  // RSS 관련 엔드포인트
                .requestMatchers("/api/quizzes/**").permitAll()  // 퀴즈 조회 관련 엔드포인트
                .requestMatchers(HttpMethod.GET, "/api/quiz-results/**").permitAll()  // 퀴즈 결과 조회
                .requestMatchers(HttpMethod.POST, "/api/quiz-results/**").authenticated()  // 퀴즈 결과 제출은 인증 필요
                .anyRequest().authenticated()
            )
            .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
} 