package org.example.flow.service.userMypage;

import lombok.RequiredArgsConstructor;
import org.example.flow.apiPayload.code.ErrorStatus;
import org.example.flow.apiPayload.exception.GeneralException;
import org.example.flow.dto.userMypage.response.CouponResponse;
import org.example.flow.dto.userMypage.response.CouponUseResponse;
import org.example.flow.entity.ReceiveCoupon;
import org.example.flow.entity.ReceiveRewardCoupon;
import org.example.flow.entity.ReceiveVisitCoupon;
import org.example.flow.repository.ReceiveCouponRepository;
import org.example.flow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class CouponService {

    private final ReceiveCouponRepository receiveCouponRepository;
    private final UserRepository userRepository;

    //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
//    public CouponResponse getCoupons(Long userId) {
//        if (userRepository.findByUserId(userId) == null) {
//            throw new GeneralException(ErrorStatus.USER_NOT_FOUND);
//        }
//        List<ReceiveCoupon> coupons = receiveCouponRepository.findByUser_UserId(userId);
//        return CouponResponse.from(coupons);
//    }

    // ✅ 쿠폰 사용   ‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
//    @Transactional
//    public CouponUseResponse useCoupon(Long userId, Long receiveCouponId, Long shopInfoId) {
//        ReceiveCoupon rc = receiveCouponRepository.findById(receiveCouponId)
//                .orElseThrow(() -> new GeneralException(ErrorStatus.COUPON_NOT_FOUND));
//
//        // 소유자 확인
//        if (rc.getUser() == null || !rc.getUser().getUserId().equals(userId)) {
//            throw new GeneralException(ErrorStatus.COUPON_NOT_OWNED);
//        }
//
//        // 이미 사용 여부
//        if (Boolean.TRUE.equals(rc.getUsed())) {
//            throw new GeneralException(ErrorStatus.COUPON_ALREADY_USED);
//        }
//
//        String type;
//        if (rc instanceof ReceiveVisitCoupon visit) {
//            type = "VISIT";
//            if (shopInfoId == null) {
//                throw new GeneralException(ErrorStatus.SHOP_ID_REQUIRED); // 없으면 새로 정의
//            }
//            //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
//            Long couponShopId = visit.getShopInfo().getShopInfoId();
//            if (!couponShopId.equals(shopInfoId)) {
//                throw new GeneralException(ErrorStatus.VISIT_SHOP_MISMATCH); // 없으면 새로 정의
//            }
//        } else if (rc instanceof ReceiveRewardCoupon) {
//            type = "REWARD";
//            // 추가 검증 없음
//        } else {
//            // 다른 서브타입이 생길 가능성 대비
//            type = rc.getClass().getSimpleName().toUpperCase();
//        }
//
//        rc.setUsed(true);
//        // 필요 시 사용 시각을 지금으로 찍고 싶다면(선택):
//        // rc.setReceiveAt(OffsetDateTime.now(ZoneOffset.UTC));
//        receiveCouponRepository.save(rc);
//
//        return CouponUseResponse.builder()
//                .receiveCouponId(rc.getReceiveCouponId())
//                .user_id(rc.getUser().getUserId())
//                .type(type)
//                .receiveAt(formatAsIso(rc.getReceiveAt()))
//                .used(true)
//                .build();
//    }

    // receiveAt이 OffsetDateTime이 아니면 프로젝트 상황에 맞춰 변환하세요.
    private String formatAsIso(Object ts) {
        if (ts == null) return null;

        if (ts instanceof OffsetDateTime odt) {
            return odt.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        if (ts instanceof java.time.LocalDateTime ldt) {
            // DB에 UTC로 저장했다고 가정하고 'Z' 부착
            return ldt.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        }
        // 그 외 타입이면 toString으로 응급처치
        return ts.toString();
    }
}
