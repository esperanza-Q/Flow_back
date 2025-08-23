package org.example.flow.service.user;

import org.example.flow.apiPayload.code.ErrorStatus;
import org.example.flow.apiPayload.exception.GeneralException;
import org.example.flow.dto.user.request.LoginRequestDTO;
import org.example.flow.dto.user.request.SignupRequestDTO;
import org.example.flow.dto.user.response.LoginResponseDTO;
import org.example.flow.dto.user.response.SignupResponseDTO;
import org.example.flow.entity.Place;
import org.example.flow.entity.ShopInfo;
import org.example.flow.entity.User;
import org.example.flow.repository.PlaceRepository;
import org.example.flow.repository.ShopInfoRepository;
import org.example.flow.repository.UserRepository;
import org.example.flow.security.CustomUserDetails;
import org.example.flow.security.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.Optional;

@Service
public class UserService implements UserDetailsService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final ShopInfoRepository shopInfoRepository;
    private final PlaceRepository placeRepository;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtTokenProvider jwtTokenProvider, ShopInfoRepository shopInfoRepository, PlaceRepository placeRepository) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtTokenProvider = jwtTokenProvider;
        this.shopInfoRepository = shopInfoRepository;
        this.placeRepository = placeRepository;
    }

    // 🔑 회원가입 (비밀번호 암호화 후 저장)
    public SignupResponseDTO signup(SignupRequestDTO requestDto) {
        User user = new User();
        user.setPassword(passwordEncoder.encode(requestDto.getPassword()));
        user.setNickname(requestDto.getNickname());
        user.setEmail(requestDto.getEmail());
        user.setRole(requestDto.getRole()); // 기본 권한 (필요 시 수정)

        userRepository.save(user);

        Long shopInfoId = null;

        // 만약 회원의 role이 SHOP이라면 ShopInfo도 생성
        if (user.getRole() == User.Role.SHOP) {
            ShopInfo shopInfo = new ShopInfo();
//            Place place = new Place();
            shopInfo.setUser(user);
            shopInfo.setMonthPayment(0);

            LocalDate now = LocalDate.now();
            int currentMonth = now.getMonthValue(); // 1 ~ 12 값 반환

            shopInfo.setNowMonth(currentMonth);
            shopInfo.setPartnershipCost(0);

            ShopInfo savedShopInfo = shopInfoRepository.save(shopInfo);

            Place place = Place.builder()
                    .shopInfo(shopInfo)
                    .location(requestDto.getLocation())
                    .category(requestDto.getCategory())
                    .longitude(requestDto.getLongitude())
                    .latitude(requestDto.getLatitude())
                    .build();

            place.setShopInfo(shopInfo);
            place.setLocation(requestDto.getLocation());

            placeRepository.save(place);
            shopInfoId = savedShopInfo.getShopInfoId();
        }

        //GENERAL
//        if (user.getRole() == User.Role.SHOP)

            return new SignupResponseDTO(user.getUserId(), shopInfoId);
    }

    // 🔐 로그인 (ID/PW 검증 후 JWT 발급)
    public LoginResponseDTO login(LoginRequestDTO requestDto) {
        User user = userRepository.findByEmail(requestDto.getEmail()).orElseThrow(()
                -> new UsernameNotFoundException("User not found with Email: " + requestDto.getEmail()));
        if (!passwordEncoder.matches(requestDto.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Invalid password");
        }
        String token = jwtTokenProvider.createToken(user.getEmail());
        return new LoginResponseDTO(user.getUserId(), user.getRole(), token);
    }

//    @Transactional
//    public void updatePassword(Long userId, String newPassword) {
//        User user = userRepository.findByUserId(userId)
//                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다."));
//
//        String encodedPassword = passwordEncoder.encode(newPassword);
//        user.setPassword(encodedPassword);
//        userRepository.save(user);
//    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 사용자 정보 조회 (username 대신 email, userId 등도 가능)
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email));

        // UserDetails로 변환
        return new CustomUserDetails(user);
    }

    public void checkUser(String email) {
        Optional<User> user = userRepository.findByEmail(email);

        if (user.isPresent()) {
            throw new GeneralException(ErrorStatus.USERNAME_ALREADY_EXISTS);
        }
    }



}
