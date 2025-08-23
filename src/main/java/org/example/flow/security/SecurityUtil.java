package org.example.flow.security;

import org.example.flow.entity.User;
import org.example.flow.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtil {

    private SecurityUtil() {
        // 유틸 클래스는 인스턴스화 방지
    }

    /**
     * 현재 SecurityContext에 저장된 Authentication 객체에서
     * 로그인한 사용자의 User 엔티티를 가져옵니다.
     *
     * @return User 엔티티
     * @throws RuntimeException 인증 정보가 없거나 타입이 잘못된 경우
     */
    public static User getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new RuntimeException("인증 정보가 없습니다.");
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof CustomUserDetails) {
            return ((CustomUserDetails) principal).getUser();
        }

        throw new RuntimeException("지원하지 않는 principal 타입: " + principal.getClass().getName());
    }

    /**
     * 현재 로그인한 사용자 ID 가져오기
     */
    public static Long getCurrentUserId() {
        return getCurrentUser().getUserId();
    }

    /**
     * 현재 로그인한 사용자 이메일 가져오기
     */
    public static String getCurrentUserEmail() {
        return getCurrentUser().getEmail();
    }
}