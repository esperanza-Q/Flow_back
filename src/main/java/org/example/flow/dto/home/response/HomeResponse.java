package org.example.flow.dto.home.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class HomeResponse {
    private String nickname;        // 닉네임
    private Integer point;          // 포인트
    private List<String> checkDate; // 방문 날짜 리스트
    private String shopName;        // 매장이름
    private Boolean openStatus;     // 오픈 여부
    private Long recommendShopId;   // 추천 아이디
    private Long shopInfoId;        // 매장정보 아이디
    private String shopImage;       // 매장이미지 URL
    private String recommendInfo;   // 한 줄 멘트
}
