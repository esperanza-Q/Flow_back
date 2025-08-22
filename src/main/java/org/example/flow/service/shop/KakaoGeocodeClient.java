// src/main/java/org/example/flow/service/shop/KakaoGeocodeClient.java
package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaoGeocodeClient implements GeocodeClient {

    @Qualifier("kakaoRestTemplate")           // ← GeocodeConfig의 @Bean(name="kakaoRestTemplate") 와 동일
    private final RestTemplate kakaoRestTemplate;

    @Override
    public GeoPoint lookup(String name, String location) {
        String query = (location != null && !location.isBlank()) ? location : name;
        if (query == null || query.isBlank()) return null;

        try {
            URI uri = UriComponentsBuilder
                    .fromUriString("https://dapi.kakao.com/v2/local/search/address.json")
                    .queryParam("query", query)
                    .build(true)
                    .toUri();

            ResponseEntity<KakaoAddressResponse> resp =
                    kakaoRestTemplate.getForEntity(uri, KakaoAddressResponse.class);

            KakaoAddressResponse body = resp.getBody();
            if (body == null || body.documents == null || body.documents.isEmpty()) return null;

            var doc = body.documents.get(0);
            Double lat = parse(doc.y); // y=lat
            Double lng = parse(doc.x); // x=lng
            return (lat != null && lng != null) ? new GeoPoint(lat, lng) : null;

        } catch (Exception e) {
            log.warn("Kakao geocode 실패. query={}. err={}", query, e.toString());
            return null;
        }
    }

    private Double parse(String v) {
        try { return (v == null) ? null : Double.parseDouble(v); }
        catch (NumberFormatException e) { return null; }
    }

    public static class KakaoAddressResponse {
        public List<Document> documents;
        public static class Document {
            public String x; // lng
            public String y; // lat
        }
    }
}
