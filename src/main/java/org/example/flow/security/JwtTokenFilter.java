package org.example.flow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        // 인증이 필요 없는 경로
        String path = request.getServletPath();
        if (path.startsWith("/api/auth/") ||
                path.startsWith("/api/users/signup") ||
                path.startsWith("/api/users/login") ||
                path.startsWith("/api/health") ||
                path.startsWith("/actuator/") ||
                path.equals("/favicon.ico") ||
                path.startsWith("/static/")) {
            filterChain.doFilter(request, response);
            return;
        }

        // JWT 토큰 가져오기
        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmail(token);

            UserDetails userDetails = userDetailsService.loadUserByUsername(email);

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
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
