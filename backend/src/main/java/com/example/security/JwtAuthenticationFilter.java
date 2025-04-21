package com.example.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        String requestUri = request.getRequestURI();
        log.info("[JWT 필터] 요청 URL: {}", requestUri);
        
        try {
            String token = jwtTokenProvider.resolveToken(request);
            log.debug("[JWT 필터] 추출된 토큰: {}", token);

            if (token != null && jwtTokenProvider.validateToken(token)) {
                Authentication auth = jwtTokenProvider.getAuthentication(token);
                SecurityContextHolder.getContext().setAuthentication(auth);
                log.info("[JWT 필터] 인증 성공: {}", auth.getName());
            } else {
                log.warn("[JWT 필터] 유효한 토큰이 없음. URI: {}", requestUri);
            }
        } catch (Exception e) {
            log.error("[JWT 필터] 토큰 처리 중 오류 발생. URI: {}", requestUri, e);
        }

        filterChain.doFilter(request, response);
    }
} 