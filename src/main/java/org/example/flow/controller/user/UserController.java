package org.example.flow.controller.user;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.example.flow.dto.user.request.LoginRequestDTO;
import org.example.flow.dto.user.request.ShopInfoWriteRequestDTO;
import org.example.flow.dto.user.request.SignupRequestDTO;
import org.example.flow.dto.user.response.LoginResponseDTO;
import org.example.flow.dto.user.response.SignupResponseDTO;
import org.example.flow.dto.user.response.UserResponseDTO;
import org.example.flow.entity.User;
import org.example.flow.security.CustomUserDetails;
import org.example.flow.security.JwtTokenProvider;
import org.example.flow.security.SecurityUtil;
import org.example.flow.service.user.ShopInfoService;
import org.example.flow.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final ShopInfoService shopInfoService;
    private final SecurityUtil securityUtil;

    // 🔑 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody SignupRequestDTO request) throws Exception {
        userService.checkUser(request.getEmail()); // 중복 체크
        SignupResponseDTO response = userService.signup(request);
        return ResponseEntity.ok(response);
    }

    // 🔐 로그인
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO request) {
        try {
            LoginResponseDTO response = userService.login(request);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // 🔓 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        // 클라이언트 측에서 JWT 삭제하도록 안내만 하면 됨
        return ResponseEntity.ok("로그아웃 성공. 클라이언트에서 토큰을 제거하세요.");
    }

    @PostMapping("/writeShopInfo")
    public ResponseEntity<String> writeShopInfo(@ModelAttribute ShopInfoWriteRequestDTO request) throws IOException {
        shopInfoService.saveShopInfo(request);
        return ResponseEntity.ok("ShopInfo 저장 성공");
    }

//    @GetMapping("/me")
//    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal CustomUserDetails userDetails) {
//        // 현재 인증된 사용자 정보 가져오기
//        User user = userDetails.getUser();
//
//        // 원하는 정보만 반환 (DTO로 감싸기)
//        UserResponseDTO response = new UserResponseDTO(user.getEmail(), user.getNickname());
//        return ResponseEntity.ok(response);
//    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser() {
        User user = securityUtil.getCurrentUser();
        UserResponseDTO response = new UserResponseDTO(user.getEmail(), user.getNickname());
        return ResponseEntity.ok(response);
    }
}