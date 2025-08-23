// src/main/java/org/example/flow/service/shop/PlaceQueryService.java
package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.response.PlaceNearbyResponse;
import org.example.flow.dto.shop.response.PlaceSimpleResponse;
import org.example.flow.entity.Place;
import org.example.flow.entity.Place.Category;
import org.example.flow.repository.PlaceRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PlaceQueryService {

    private final PlaceRepository placeRepository;

    /** 전체/카테고리별 페이지 조회 */
    public Page<PlaceSimpleResponse> getPlaces(String category, Pageable pageable) {
        Page<Place> page = (category == null || category.isBlank())
                ? placeRepository.findAll(pageable)
                : placeRepository.findByCategory(parseCategory(category), pageable);

        return page.map(this::toSimpleDto);
    }

    /** 근처 장소(거리순) */
    public List<PlaceNearbyResponse> getNearby(
            double lat,
            double lng,
            String category,          // null 허용
            Integer maxDistanceMeters, // null 허용
            Integer limit              // null → 기본값 50
    ) {
        int safeLimit = (limit == null || limit <= 0) ? 50 : Math.min(limit, 200);

        // ENUM 문자열 그대로 쿼리에 전달 (null 허용)
        String categoryString = (category == null || category.isBlank())
                ? null
                : parseCategory(category).name();

        var rows = placeRepository.findNearby(lat, lng, categoryString, maxDistanceMeters, safeLimit);

        return rows.stream()
                .map(r -> new PlaceNearbyResponse(
                        r.getPlaceId(),
                        r.getExplanationTitle(),
                        r.getLocation(),
                        r.getCategory(),      // DB 문자열 그대로
                        r.getLatitude(),
                        r.getLongitude(),
                        r.getDistanceMeters()
                ))
                .toList();
    }

    // === helpers ===
    private PlaceSimpleResponse toSimpleDto(Place p) {
        return new PlaceSimpleResponse(
                p.getPlaceId(),
                p.getExplanationTitle(),
                p.getLocation(),
                p.getCategory() != null ? p.getCategory().name() : null,
                p.getLatitude(),
                p.getLongitude()
        );
    }

    private Category parseCategory(String category) {
        try {
            return Category.valueOf(category.trim().toUpperCase());
        } catch (Exception e) {
            // 잘못된 값 오면 ECT로 폴백 (또는 예외)
            return Category.ECT;
        }
    }
}
