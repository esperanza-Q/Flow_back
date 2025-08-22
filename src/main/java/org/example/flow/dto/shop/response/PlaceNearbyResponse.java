package org.example.flow.dto.shop.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Builder
public class PlaceNearbyResponse {
    private Long placeId;
    private String name;
    private String location;
    private Double latitude;
    private Double longitude;
    private long distanceMeters; // 현재 위치로부터 거리
}
