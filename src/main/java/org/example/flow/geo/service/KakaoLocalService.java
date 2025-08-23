package org.example.flow.geo.service;

import org.example.flow.geo.dto.KakaoLocalDtos.AddressSearchResponse;
import org.example.flow.geo.dto.KakaoLocalDtos.KeywordSearchResponse;
import org.example.flow.geo.dto.KakaoLocalDtos.ReverseGeocodeResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;


@Service
public class KakaoLocalService {


    private final WebClient kakaoWebClient;


    public KakaoLocalService(WebClient kakaoWebClient) {
        this.kakaoWebClient = kakaoWebClient;
    }


    /** 주소 → 좌표 */
    public AddressSearchResponse geocode(String address) {
        try {
            return kakaoWebClient.get()
                    .uri(uri -> uri.path("/v2/local/search/address.json")
                            .queryParam("query", address)
                            .build())
                    .retrieve()
                    .onStatus(status -> status.isError(), r -> r.createException().flatMap(Mono::error))
                    .bodyToMono(AddressSearchResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new IllegalStateException("Kakao address search failed: " + e.getResponseBodyAsString(), e);
        }
    }
    /** 좌표 → 주소 */
    public ReverseGeocodeResponse reverseGeocode(double lat, double lng) {
        try {
            return kakaoWebClient.get()
                    .uri(uri -> uri.path("/v2/local/geo/coord2address.json")
                            .queryParam("y", lat) // lat = y
                            .queryParam("x", lng) // lng = x
                            .build())
                    .retrieve()
                    .onStatus(status -> status.isError(), r -> r.createException().flatMap(Mono::error))
                    .bodyToMono(ReverseGeocodeResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new IllegalStateException("Kakao reverse geocode failed: " + e.getResponseBodyAsString(), e);
        }
    }


    /** 키워드 장소 검색 (옵션: 중심좌표+반경 미터) */
    public KeywordSearchResponse searchPlace(String query, Double lat, Double lng, Integer radius) {
        try {
            return kakaoWebClient.get()
                    .uri((UriBuilder uri) -> {
                        UriBuilder b = uri.path("/v2/local/search/keyword.json")
                                .queryParam("query", query); // 필수 파라미터

                        if (lat != null && lng != null) {
                            b = b.queryParam("y", lat)  // lat = y
                                    .queryParam("x", lng); // lng = x
                        }
                        if (radius != null) {
                            b = b.queryParam("radius", radius); // 0~20000m
                        }
                        return b.build();
                    })
                    .retrieve()
                    .onStatus(status -> status.isError(), r -> r.createException().flatMap(Mono::error))
                    .bodyToMono(KeywordSearchResponse.class)
                    .block();
        } catch (WebClientResponseException e) {
            throw new IllegalStateException("Kakao keyword search failed: " + e.getResponseBodyAsString(), e);
        }
    }
}