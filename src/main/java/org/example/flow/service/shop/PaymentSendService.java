package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.response.PaymentSendResponse;
import org.example.flow.entity.PaymentCheck;
import org.example.flow.entity.ShopInfo;
import org.example.flow.entity.User;
import org.example.flow.repository.PaymentCheckRepository;
import org.example.flow.repository.ShopInfoRepository;
import org.example.flow.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneOffset;

@Service
@RequiredArgsConstructor
public class PaymentSendService {

    private final PaymentCheckRepository paymentCheckRepository;
    private final UserRepository userRepository;
    private final ShopInfoRepository shopInfoRepository;

    @Transactional
    public PaymentSendResponse createPaymentCheck(Long pathShopInfoId, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        ShopInfo shopInfo = shopInfoRepository.findById(pathShopInfoId)
                .orElseThrow(() -> new IllegalArgumentException("ShopInfo not found: " + pathShopInfoId));

        PaymentCheck pc = new PaymentCheck();
        pc.setUser(user);
        pc.setShopInfo(shopInfo);
        pc.setAmount(3); // 기본값 3
        pc.setStatus(PaymentCheck.STATUS.WAITING);
        // createdAt 은 @PrePersist 로 자동 세팅

        PaymentCheck saved = paymentCheckRepository.save(pc);

        // Mapper 없이 여기서 바로 DTO 생성
        String createdAtUtc = saved.getCreatedAt()
                .atOffset(ZoneOffset.UTC)
                .toInstant()
                .toString(); // 예: 2025-08-21T01:23:45Z

        return new PaymentSendResponse(
                saved.getPaymentCheckId(),
                saved.getUser().getUserId(),
                saved.getShopInfo().getShopInfoId(),
                saved.getAmount(),
                saved.getStatus().name(),
                createdAtUtc
        );
    }
}
