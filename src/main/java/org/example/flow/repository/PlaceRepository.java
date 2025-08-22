package org.example.flow.repository;

import org.example.flow.entity.Place;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;


public interface PlaceRepository extends JpaRepository<Place, Long> {
    List<Place> findByLocationContaining(String district);

    List<Place> findByCategory(Place.Category category);

    List<Place> findByLocationContainingAndCategory(String district, Place.Category category);

    Page<Place> findAllByCategory(Place.Category category, Pageable pageable);

    Page<Place> findByCategory(String category, Pageable pageable);

    Place findByShopInfo_ShopInfoId(Long shopInfoId);



    Page<Place> findByLatitudeIsNullOrLongitudeIsNull(Pageable pageable);
    // 좌표 미완성 행을 키셋으로 소량 조회 (ORDER BY place_id)

    // (A) 지오코딩 대상 조회: afterId 이후, 위경도 비어있는 행만 pageSize 만큼
    @Query(value = """

            SELECT 
            p.place_id   AS placeId,
            p.name       AS name,
            p.location   AS location
            FROM place p
            WHERE (p.latitude IS NULL OR p.longitude IS NULL)
          AND p.place_id > :afterId
            ORDER BY p.place_id ASC
            LIMIT :pageSize
        """, nativeQuery = true)
    List<Object[]> findTargets(@Param("afterId") long afterId,
                               @Param("pageSize") int pageSize);

    // (B) 위경도 업데이트(아직 NULL인 경우에만)
    @Transactional
    @Modifying
    @Query(value = """

            UPDATE place
        SET latitude = :lat, longitude = :lng
        WHERE place_id = :placeId
          AND (latitude IS NULL OR longitude IS NULL)
        """, nativeQuery = true)
    int updateCoordsIfNull(@Param("placeId") long placeId,
                           @Param("lat") Double lat,
                           @Param("lng") Double lng);

    // (C) 근처 장소 조회(Haversine)
    @Query(value = """
        SELECT 
            p.place_id AS placeId,
            p.name     AS name,
            p.location AS location,
            p.latitude AS latitude,
            p.longitude AS longitude,
            (6371000 * acos(
                cos(radians(:lat)) * cos(radians(p.latitude))
              * cos(radians(p.longitude) - radians(:lng))
              + sin(radians(:lat)) * sin(radians(p.latitude))
            )) AS distanceMeters
        FROM place p
        WHERE (:category IS NULL OR p.category = :category)
        HAVING (:radius IS NULL OR distanceMeters <= :radius)
        ORDER BY distanceMeters ASC
        LIMIT :limit
        """, nativeQuery = true)
    List<Object[]> findNearby(@Param("lat") double lat,
                              @Param("lng") double lng,
                              @Param("limit") int limit,
                              @Param("radius") Integer radius,
                              @Param("category") String category);

    }
