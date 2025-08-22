package org.example.flow.dto.shop;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class MapPlaceListResponse {
    private String message;
    private List<PlaceMarkerResponse> data;
}

