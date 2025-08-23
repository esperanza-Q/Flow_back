package org.example.flow.dto.shop.response;

import lombok.*;

@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
public class PlaceSimpleResponse {
    private Long placeId;
    private String explanationTitle; // ← 엔티티의 explanationTitle
    private String location;
    private String category;         // ENUM → 문자열
    private Double latitude;
    private Double longitude;
}