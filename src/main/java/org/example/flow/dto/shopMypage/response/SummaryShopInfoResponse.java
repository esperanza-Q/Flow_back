package org.example.flow.dto.shopMypage.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class SummaryShopInfoResponse {

    private String shopInfoId;   // 매장 이름

    private NowMonth nowMonth;

    private Integer monthPayment;

    private Integer partnershipCost;

    @Data
    @AllArgsConstructor
    public static class NowMonth {
        private String month; // "2025-08"
        private String from;  // "2025-08-01"
        private String to;    // "2025-08-31"
    }
}
