// org.example.flow.service.shop.PaymentSendService (필요 시 보강)
package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.entity.PaymentCheck;
import org.example.flow.entity.ShopInfo;
import org.example.flow.entity.User;
import org.example.flow.repository.PaymentCheckRepository;
import org.example.flow.repository.ShopInfoRepository;
import org.example.flow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class PaymentSendService {

    private final PaymentCheckRepository paymentCheckRepository;
    private final UserRepository userRepository;
    private final ShopInfoRepository shopInfoRepository;

    @Transactional
    public PaymentCheck createPaymentCheck(Long shopInfoId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("user not found: " + userId));
        ShopInfo shop = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new IllegalArgumentException("shopInfo not found: " + shopInfoId));

        PaymentCheck pc = new PaymentCheck();
        pc.setUser(user);
        pc.setShopInfo(shop);
        // amount는 아직 모름 → null 또는 0 (너의 컬럼이 NOT NULL이면 0으로)
        pc.setAmount(null);
        pc.setStatus(PaymentCheck.STATUS.WAITING);
        // createdAt은 @PrePersist에서 자동 세팅 (엔티티에 이미 있음)
        return paymentCheckRepository.save(pc);
    }

    // ISO8601 UTC 문자열로 바꾸고 싶을 때 쓸 유틸(컨트롤러에서 사용)
    public static String toUtcString(java.time.LocalDateTime ldt) {
        if (ldt == null) return OffsetDateTime.now(ZoneOffset.UTC).toString();
        return ldt.atOffset(ZoneOffset.UTC).toString();
    }
}
