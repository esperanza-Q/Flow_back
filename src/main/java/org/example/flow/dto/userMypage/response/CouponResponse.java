package org.example.flow.dto.userMypage.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.flow.entity.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@AllArgsConstructor
public class CouponResponse {
    private List<CouponDto> content;

    //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
//    public static CouponResponse from(List<ReceiveCoupon> coupons) {
//        return new CouponResponse(coupons.stream().map(CouponDto::from).toList());
//    }

    @Getter
    @AllArgsConstructor
    public static class CouponDto {
        private Long receiveCouponId;
        @JsonProperty("user_id")
        private Long userId;
        private String type;
        private LocalDateTime receiveAt;
        private Boolean used;
        private Reward reward; // REWARD일 때만 값
        private Visit visit;   // VISIT일 때만 값


        //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
//        public static CouponDto from(ReceiveCoupon c) {
//            String type;
//            Reward reward = null;
//            Visit visit = null;
//
//            if (c instanceof ReceiveRewardCoupon rc) {
//                type = "REWARD";
//                reward = new Reward(rc.getRewardCoupon().getRewardCouponId());
//            } else if (c instanceof ReceiveVisitCoupon vc) {
//                type = "VISIT";
//                //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
//                visit = new Visit(vc.getShopInfo().getShopInfoId());
//            } else {
//                // 자식 타입이 아니면 추천 쿠폰(부가정보 없음)
//                type = "RECOMMEND";
//            }
//
//            return new CouponDto(
//                    c.getReceiveCouponId(),
//                    c.getUser().getUserId(),
//                    type,
//                    c.getReceiveAt(),
//                    c.getUsed(),
//                    reward,
//                    visit
//            );
//        }


        @Getter @AllArgsConstructor
        public static class Reward {
            @JsonProperty("rewardCoupon_id")
            private Long rewardCouponId;
        }
        @Getter @AllArgsConstructor
        public static class Visit {
            @JsonProperty("shopInfo_id")
            private Long shopInfoId;
        }
    }
}
