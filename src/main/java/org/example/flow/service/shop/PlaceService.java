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

    /** 성북구 전체 마커 (좌표 있는 것만) */
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
     * 레포지토리 시그니처가 enum을 받으므로 enum 그대로 전달
     */
    public List<PlaceMarkerResponse> search(String district, Place.Category category) {
        final boolean hasDistrict = district != null && !district.isBlank();
        final boolean hasCategory = category != null;

        List<Place> rows;
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
        Long shopInfoId = (p.getShopInfo() != null) ? p.getShopInfo().getShopInfoId() : null;

        // 카테고리 문자열(소문자). null이면 "etc"로
        String type = (p.getCategory() != null) ? p.getCategory().name().toLowerCase() : "etc";

        // 타이틀: explanationTitle 우선, 없으면 location
        String title = (p.getExplanationTitle() != null && !p.getExplanationTitle().isBlank())
                ? p.getExplanationTitle()
                : p.getLocation();

        return new PlaceMarkerResponse(
                shopInfoId,
                type,
                p.getLatitude(),
                p.getLongitude(),
                title
        );
    }
}
