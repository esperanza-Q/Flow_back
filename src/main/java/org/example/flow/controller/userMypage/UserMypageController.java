package org.example.flow.controller.userMypage;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.userMypage.request.UseCouponRequest;
import org.example.flow.dto.userMypage.response.CouponUseResponse;
import org.example.flow.dto.userMypage.response.UserMypageResponse;
import org.example.flow.dto.userMypage.response.CouponResponse;
import org.example.flow.service.userMypage.UserMypageService;
import org.example.flow.service.userMypage.CouponService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserMypageController {

    private final UserMypageService userMypageService;
    private final CouponService couponService; // 추가

    // GET /api/user/{user_id}/userMypage
    @GetMapping("/user/{user_id}/userMypage")
    public UserMypageResponse getUserMypage(@PathVariable("user_id") Long userId) {
        return userMypageService.getUserMypage(userId);
    }

    //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
    // GET /api/user/{user_id}/userMypage/coupon
//    @GetMapping("/user/{user_id}/userMypage/coupon")
//    public CouponResponse getUserCoupons(@PathVariable("user_id") Long userId) {
//        return couponService.getCoupons(userId);
//    }

    // ✅  쿠폰 사용   ‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
//    @PatchMapping("/user/{user_id}/mypage/coupons/{receiveCouponId}/use")
//    public CouponUseResponse useCoupon(
//            @PathVariable("user_id") Long userId,
//            @PathVariable Long receiveCouponId,
//            @RequestBody UseCouponRequest request
//    ) {
//        //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
//        return couponService.useCoupon(userId, receiveCouponId, request.getShopInfoId());
//    }
}
