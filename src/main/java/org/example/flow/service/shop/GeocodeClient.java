// src/main/java/org/example/flow/service/shop/GeocodeClient.java
package org.example.flow.service.shop;

/**
 * 주소/이름을 기반으로 위도(lat), 경도(lng)를 조회하는 클라이언트 인터페이스.
 * 구현체 예: KakaoGeocodeClient, DummyGeocodeClient 등
 */
public interface GeocodeClient {

    /**
     * @param name     장소명(선택)
     * @param location 주소 문자열(선택). location이 비어있지 않으면 우선 사용.
     * @return 좌표가 있으면 GeoPoint, 없으면 null
     */
    GeoPoint lookup(String name, String location);

    /**
     * 위/경도 좌표 DTO. (Java 16+ record)
     */
    record GeoPoint(Double lat, Double lng) {}
}
