package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.PlaceSimpleResponse;
import org.example.flow.dto.shop.response.PlaceNearbyResponse;
import org.example.flow.entity.Place;
import org.example.flow.repository.PlaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.example.flow.service.shop.PlaceQueryService;


import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceQueryService {

    private final PlaceRepository placeRepository;

    /** 기존 전체 조회 */
    public Page<PlaceSimpleResponse> getPlaces(String category, Pageable pageable) {
        Page<Place> page = (category == null)
                ? placeRepository.findAll(pageable)
                : placeRepository.findByCategory(category, pageable); // 필요시 추가

        return page.map(p -> new PlaceSimpleResponse(
                p.getPlaceId(),
                p.getName(),
                p.getLocation(),
                p.getCategory() != null ? p.getCategory().name() : null, // ← category를 String으로
                p.getLatitude(),
                p.getLongitude()
        ));
    }

    /** ✅ 가까운 곳 조회 (컨트롤러와 시그니처 1:1 일치) */
    public List<PlaceNearbyResponse> getNearbyPlaces(double lat, double lng,
                                                     int limit, Integer radius, String category) {
        var rows = placeRepository.findNearby(lat, lng, limit, radius, category);
        return rows.stream().map(r -> new PlaceNearbyResponse(
                ((Number) r[0]).longValue(),               // placeId
                (String) r[1],                             // name
                (String) r[2],                             // location
                r[3] != null ? ((Number) r[3]).doubleValue() : null, // latitude
                r[4] != null ? ((Number) r[4]).doubleValue() : null, // longitude
                r[5] != null ? Math.round(((Number) r[5]).doubleValue()) : -1 // distanceMeters
        )).toList();
    }
}
