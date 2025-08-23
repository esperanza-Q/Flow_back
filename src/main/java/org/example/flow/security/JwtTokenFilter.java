package org.example.flow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.flow.entity.User;
import org.example.flow.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public JwtTokenFilter(JwtTokenProvider jwtTokenProvider, UserDetailsService userDetailsService, UserRepository userRepository) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

//     JWT 검증
@Override
protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    // 인증이 필요 없는 경로는 필터 건너뛰기
    if (httpRequest.getServletPath().startsWith("/api/auth/") ||
            httpRequest.getServletPath().startsWith("/api/health") ||
            httpRequest.getServletPath().startsWith("/actuator/") ||
            httpRequest.getServletPath().startsWith("/api/users/signup") ||
            httpRequest.getServletPath().startsWith("/api/users/login")) {
        filterChain.doFilter(httpRequest, httpResponse);
        return;
    }

    // 🔹 여기서 JWT 토큰 가져오기
    String token = jwtTokenProvider.resolveToken(httpRequest);
    System.out.println("JWT token: " + token);  // 디버깅용

    // 🔹 토큰이 유효하면 인증 처리
    if (token != null && jwtTokenProvider.validateToken(token)) {
        String email = jwtTokenProvider.getEmail(token);
        System.out.println("JWT email: " + email);  // 디버깅용

        // 🔹 UserDetails 가져오기
        UserDetails userDetails = userDetailsService.loadUserByUsername(email);

        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));

        // 🔹 SecurityContext에 인증 객체 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    // 다음 필터로 넘어가기
    filterChain.doFilter(httpRequest, httpResponse);
}
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
//            throws ServletException, IOException {
//
//        HttpServletRequest httpRequest = (HttpServletRequest) request;
//        HttpServletResponse httpResponse = (HttpServletResponse) response;
//
//        // 인증이 필요 없는 경로는 필터 건너뛰기
//        if (httpRequest.getServletPath().startsWith("/api/auth/") ||
//                httpRequest.getServletPath().startsWith("/api/health") ||
//                httpRequest.getServletPath().startsWith("/actuator/") ||
//                httpRequest.getServletPath().startsWith("/api/users/")) {
//            filterChain.doFilter(httpRequest, httpResponse);
//            return;
//        }
//        System.out.println("JwtTokenFilter 실행됨");
//
//        // JWT 받기
//        String token = jwtTokenProvider.resolveToken(httpRequest);
//
//// 유효한 토큰인지 확인
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            // 토큰에서 email 가져옴
//            String email = jwtTokenProvider.getEmail(token);
//
//            // userId로 User 정보 가져와서 userDetails에 저장
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
//                    userDetails, null, userDetails.getAuthorities());
//
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpRequest));
//
//            // **로그 추가**
//            System.out.println("JWT token: " + token);
//            System.out.println("Email from token: " + email);
//            System.out.println("Authentication object: " + authentication);
//
//            // SecurityContext에 Authentication 저장
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//    }
}