package org.example.flow.controller.user;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.user.request.LoginRequestDTO;
import org.example.flow.dto.user.request.SignupRequestDTO;
import org.example.flow.dto.user.response.LoginResponseDTO;
import org.example.flow.dto.user.response.SignupResponseDTO;
import org.example.flow.entity.User;
import org.example.flow.security.JwtTokenProvider;
import org.example.flow.service.user.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/users", produces = "application/json")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 🔑 회원가입
    @PostMapping("/signup")
    public ResponseEntity<?> register(@RequestBody SignupRequestDTO request) {
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
}