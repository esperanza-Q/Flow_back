package org.example.flow.service.user;

import org.example.flow.entity.User;
import org.example.flow.repository.UserRepository;
import org.example.flow.security.CustomUserDetails;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@Primary
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

//    @Override
//    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
//        User user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + email));
//        return new CustomUserDetails(user); // 네가 만든 CustomUserDetails 사용
//    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException(email));

        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // 여기서 무조건 CustomUserDetails 반환
        // 🔹 로그: 반환되는 UserDetails 타입
        System.out.println("[CustomUserDetailsService] Returning: " + customUserDetails.getClass().getName());


        return new CustomUserDetails(user);
    }
}

