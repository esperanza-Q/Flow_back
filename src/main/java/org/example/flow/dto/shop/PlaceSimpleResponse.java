package org.example.flow.dto.shop;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

// 순서: id, name, location, category, latitude, longitude
@AllArgsConstructor
@Getter
@Builder
public class PlaceSimpleResponse {
    private Long placeId;
    private String name;
    private String location;
    private String category;
    private Double latitude;
    private Double longitude;
}


