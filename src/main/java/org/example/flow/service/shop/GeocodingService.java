// src/main/java/org/example/flow/service/shop/GeocodingService.java
package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class GeocodingService {
    private final GeocodeClient geocodeClient; // ← 타입이 GeocodeClient 여야 함(구현체 아님)

    public GeocodeClient.GeoPoint find(String name, String location) {
        return geocodeClient.lookup(name, location);
    }
}
