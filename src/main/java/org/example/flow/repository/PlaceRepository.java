package org.example.flow.repository;

import org.example.flow.entity.Place;
import org.example.flow.entity.ShopInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;


public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByLocationContaining(String district);

    List<Place> findByCategory(Place.Category category);

    List<Place> findByLocationContainingAndCategory(String district, Place.Category category);

    Place findByShopInfo(ShopInfo shopInfo);

    Page<Place> findByCategory(Place.Category category, Pageable pageable);

    Page<Place> findAll(Pageable pageable);

    Optional<Place> findByShopInfoShopInfoId(Long shopInfoId);


    /** 네이티브: 반경/거리 기반 근처 장소 조회 */
    @Query(value = """
        /* dynamic native SQL query */
        SELECT
            p.place_id           AS placeId,
            p.explanation_title  AS explanationTitle,
            p.location           AS location,
            p.category           AS category,
            p.latitude           AS latitude,
            p.longitude          AS longitude,
            (6371000 * ACOS(
                COS(RADIANS(:lat)) * COS(RADIANS(p.latitude))
              * COS(RADIANS(p.longitude) - RADIANS(:lng))
              + SIN(RADIANS(:lat)) * SIN(RADIANS(p.latitude))
            ))                   AS distanceMeters
        FROM place p
        /* 위경도 NULL 제외(계산 불가) */
        WHERE p.latitude IS NOT NULL
          AND p.longitude IS NOT NULL
          AND (:category IS NULL OR p.category = :category)
        HAVING (:maxDistance IS NULL OR distanceMeters <= :maxDistance)
        ORDER BY distanceMeters ASC
        LIMIT :limit
        """,
            nativeQuery = true)
    List<NearbyProjection> findNearby(
            @Param("lat") double lat,
            @Param("lng") double lng,
            @Param("category") String category,      // ENUM 문자열 (예: "CAFE")
            @Param("maxDistance") Integer maxDistance, // 미터
            @Param("limit") int limit
    );

    /** 네이티브 결과를 인터페이스 프로젝션으로 받기 */
    interface NearbyProjection {
        Long getPlaceId();
        String getExplanationTitle();
        String getLocation();
        String getCategory();
        Double getLatitude();
        Double getLongitude();
        Double getDistanceMeters();
    }
}