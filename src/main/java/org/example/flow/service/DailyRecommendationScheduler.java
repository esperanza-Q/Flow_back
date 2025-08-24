package org.example.flow.service;

import lombok.RequiredArgsConstructor;
import org.example.flow.entity.User;
import org.example.flow.repository.UserRepository;
import org.example.flow.service.recommendShop.ShopRecommendationService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DailyRecommendationScheduler {

    private final UserRepository userRepository;
    private final ShopRecommendationService recommendationService;

    // 매일 0시(자정)마다 실행
//    @Scheduled(cron = "0 0 0 * * ?")
    @Scheduled(cron = "0 0 0 * * ?")
    public void recommendShopsForAllUsers() {
        LocalDateTime now = LocalDateTime.now();

        List<User> generalUsers = userRepository.findByRole(User.Role.GENERAL);

        for (User user : generalUsers) {
            recommendationService.recommendShop(user, now);
        }
    }
}

