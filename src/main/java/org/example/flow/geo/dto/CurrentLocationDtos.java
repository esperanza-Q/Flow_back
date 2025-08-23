package org.example.flow.geo.dto;


import jakarta.validation.constraints.NotNull;


public class CurrentLocationDtos {
    public record CurrentLocationRequest(
            @NotNull Double lat,
            @NotNull Double lng
    ) {}


    public record CurrentLocationResponse(
            double lat,
            double lng,
            String addressName,
            String region1,
            String region2,
            String region3,
            String roadAddressName
    ) {}
}
