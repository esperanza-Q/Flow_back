package org.example.flow.dto.userMypage.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.example.flow.entity.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CouponResponse {
    private List<CouponDto> content;

    public static CouponResponse from(List<ReceiveCoupon> coupons) {
        return new CouponResponse(
                coupons.stream().map(CouponDto::from).collect(Collectors.toList())
        );
    }

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


        public static CouponDto from(ReceiveCoupon c) {
            String type;
            Reward reward = null;
            Visit visit = null;

            if (c instanceof ReceiveRewardCoupon) {
                ReceiveRewardCoupon rc = (ReceiveRewardCoupon) c;
                type = "REWARD";
                if (rc.getRewardCoupon() != null) {
                    reward = new Reward(rc.getRewardCoupon().getRewardCouponId());
                }
            } else if (c instanceof ReceiveVisitCoupon) {
                ReceiveVisitCoupon vc = (ReceiveVisitCoupon) c;
                Long shopInfoId = null;
                if (vc.getBenefitReq() != null && vc.getBenefitReq().getShopInfo() != null) {
                    shopInfoId = vc.getBenefitReq().getShopInfo().getShopInfoId();
                }
                type = "VISIT";
                if (shopInfoId != null) visit = new Visit(shopInfoId);

            } else {
                // 다른(추천) 타입
                type = "RECOMMEND";
            }

            Long userId = (c.getUser() != null) ? c.getUser().getUserId() : null;

            return new CouponDto(
                    c.getReceiveCouponId(),
                    userId,
                    type,
                    c.getReceiveAt(),
                    c.getUsed(),
                    reward,
                    visit
            );
        }



        @Getter @AllArgsConstructor
        public static class Reward {
            @JsonProperty("rewardCoupon_id")
            private Long rewardCouponId;
        }
        @Getter @AllArgsConstructor
        public static class Visit {
            @JsonProperty("shop_info_id")
            private Long shopInfoId;
        }
    }
}
