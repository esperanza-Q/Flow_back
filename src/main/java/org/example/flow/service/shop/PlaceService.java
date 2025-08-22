package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.PlaceMarkerResponse;
import org.example.flow.entity.Place;
import org.example.flow.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PlaceService {

    private final PlaceRepository placeRepository;

    /**
     * 성북구 전체 마커 (좌표 있는 것만)
     */
    public List<PlaceMarkerResponse> getSeongbukAll() {
        return placeRepository.findByLocationContaining("성북구").stream()
                .filter(this::hasCoordinates)
                .map(this::toMarker)
                .toList();
    }

    /**
     * (옵션) 구/카테고리 검색
     * - district: "성북구" 같은 구 단위 텍스트 (null/blank 허용)
     * - category: Place.Category (null 허용)
     */
    public List<PlaceMarkerResponse> search(String district, Place.Category category) {
        List<Place> rows;

        boolean hasDistrict = district != null && !district.isBlank();
        boolean hasCategory = category != null;

        if (hasDistrict && hasCategory) {
            rows = placeRepository.findByLocationContainingAndCategory(district, category);
        } else if (hasDistrict) {
            rows = placeRepository.findByLocationContaining(district);
        } else if (hasCategory) {
            rows = placeRepository.findByCategory(category);
        } else {
            rows = placeRepository.findAll();
        }

        return rows.stream()
                .filter(this::hasCoordinates)
                .map(this::toMarker)
                .toList();
    }

    // --- helpers ---

    private boolean hasCoordinates(Place p) {
        return p.getLatitude() != null && p.getLongitude() != null;
    }

    private PlaceMarkerResponse toMarker(Place p) {
        // ShopInfo 가 없을 수 있으므로 null-safe 처리 (DTO가 Long라면 null 가능)
        Long shopInfoId = (p.getShopInfo() != null) ? p.getShopInfo().getShopInfoId() : null;

        // 카테고리 소문자
        String type = (p.getCategory() != null) ? p.getCategory().name().toLowerCase() : "ect";

        // 표시 이름: name 필드가 있으면 name, 없으면 location 사용
        String title = (getSafeName(p) != null && !getSafeName(p).isBlank())
                ? getSafeName(p)
                : p.getLocation();

        return new PlaceMarkerResponse(
                shopInfoId,          // null일 수 있음 (DTO 타입이 Long이어야 함)
                type,
                p.getLatitude(),
                p.getLongitude(),
                title
        );
    }

    // Place에 name 필드가 없을 수도 있으니 안전하게 분기
    private String getSafeName(Place p) {
        try {
            return p.getName(); // Place에 name 필드가 있는 설계(권장)
        } catch (NoSuchMethodError | RuntimeException e) {
            return null; // name 없으면 null 반환하고 location으로 대체
        }
    }
}
