package org.example.flow.geo.controller;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.example.flow.geo.dto.KakaoLocalDtos;
import org.example.flow.geo.service.KakaoLocalService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/geo")
@Validated
public class GeoController {


    private final KakaoLocalService kakaoLocalService;


    public GeoController(KakaoLocalService kakaoLocalService) {
        this.kakaoLocalService = kakaoLocalService;
    }


    // GET /api/geo/geocode?address=서울 성북구 성북로 156 1층
    @GetMapping("/geocode")
    public KakaoLocalDtos.AddressSearchResponse geocode(@RequestParam @NotBlank String address) {
        return kakaoLocalService.geocode(address);
    }


    // GET /api/geo/reverse?lat=37.592&lng=127.005
    @GetMapping("/reverse")
    public KakaoLocalDtos.ReverseGeocodeResponse reverse(
            @RequestParam @NotNull Double lat,
            @RequestParam @NotNull Double lng) {
        return kakaoLocalService.reverseGeocode(lat, lng);
    }


    // GET /api/geo/search?query=스타벅스&lat=37.59&lng=127.00&radius=1000
    @GetMapping("/search")
    public KakaoLocalDtos.KeywordSearchResponse search(
            @RequestParam @NotBlank String query,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Integer radius) {
        return kakaoLocalService.searchPlace(query, lat, lng, radius);
    }
}