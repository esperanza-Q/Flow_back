package org.example.flow.controller.home;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.home.response.HomeResponse;
import org.example.flow.dto.home.response.RecommendShopResponse;
import org.example.flow.entity.User;
import org.example.flow.security.SecurityUtil;
import org.example.flow.service.home.HomeRecommendService;
import org.example.flow.service.home.HomeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;
    private final SecurityUtil securityUtil;
    private final HomeRecommendService homeRecommendService;

    @GetMapping("/home")
    public HomeResponse getHome() {
        User current = securityUtil.getCurrentUser();  // 로그인 필수 (토큰 받아와서 테스트......)
        return homeService.getHome(current.getUserId());
    }

    @GetMapping("/home/recommendShop")
    public RecommendShopResponse getRecommendShop(@RequestParam Long recommendShopId) {
        return homeRecommendService.getRecommendShop(recommendShopId);
    }
}