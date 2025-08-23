package org.example.flow.dto.shop;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.flow.entity.Place;

@Getter
@AllArgsConstructor
public class PlaceMarkerResponse {

    @JsonProperty("shopInfo_id")
    private Long shopInfoId;          // Place.shopInfo.shopInfoId (숫자 PK)

    private String category;          // enum → 소문자 문자열
    private double latitude;
    private double longitude;

    @JsonProperty("address")
    private String address;           // Place.location

    public static PlaceMarkerResponse from( // 필요시 서비스/리포지토리에서 사용
                                            Long shopInfoId, Place.Category category, Double lat, Double lng, String location
    ) {
        return new PlaceMarkerResponse(
                shopInfoId,
                category.name().toLowerCase(),
                lat,
                lng,
                location
        );
    }
}