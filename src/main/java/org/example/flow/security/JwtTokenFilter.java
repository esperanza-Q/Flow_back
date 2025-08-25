package org.example.flow.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.flow.entity.User;
import org.example.flow.repository.UserRepository;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

//@Component
//@RequiredArgsConstructor
//public class JwtTokenFilter extends OncePerRequestFilter {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final UserDetailsService userDetailsService;
//
//    @Override
//    protected void doFilterInternal(HttpServletRequest request,
//                                    HttpServletResponse response,
//                                    FilterChain filterChain) throws ServletException, IOException {
//
//        // 인증이 필요 없는 경로
//        String path = request.getServletPath();
//        if (path.startsWith("/api/auth/") ||
//                path.startsWith("/api/users/signup") ||
//                path.startsWith("/api/users/login") ||
//                path.startsWith("/api/health") ||
//                path.startsWith("/actuator/") ||
//                path.equals("/favicon.ico") ||
//                path.startsWith("/static/")) {
//            filterChain.doFilter(request, response);
//            return;
//        }
//
//        // JWT 토큰 가져오기
//        String token = jwtTokenProvider.resolveToken(request);
//
//        if (token != null && jwtTokenProvider.validateToken(token)) {
//            String email = jwtTokenProvider.getEmail(token);
//
//            UserDetails userDetails = userDetailsService.loadUserByUsername(email);
//
//            UsernamePasswordAuthenticationToken authentication =
//                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//}
@Component
@RequiredArgsConstructor
public class JwtTokenFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        String token = jwtTokenProvider.resolveToken(request);

        if (token != null && jwtTokenProvider.validateToken(token)) {
            String email = jwtTokenProvider.getEmail(token);
            Object userDetails = userDetailsService.loadUserByUsername(email);

            // 🔹 로그: principal 타입
            System.out.println("[JwtTokenFilter] loadUserByUsername returns: " + userDetails.getClass().getName());

            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(userDetails, null,
                            userDetails instanceof UserDetails ? ((UserDetails) userDetails).getAuthorities() : null);

            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 🔹 로그: SecurityContextHolder에 있는 Authentication 타입
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("[JwtTokenFilter] SecurityContext Authentication: " +
                (auth != null ? auth.getClass().getName() + ", principal=" + auth.getPrincipal().getClass().getName() : "null"));

        filterChain.doFilter(request, response);
    }
}