package org.example.flow.dto.shopMypage.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AcceptPaymentRequest {
    private Integer amount; // 필수: 0보다 큰 값
}
