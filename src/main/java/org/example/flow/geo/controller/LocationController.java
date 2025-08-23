package org.example.flow.geo.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.flow.geo.dto.CurrentLocationDtos.CurrentLocationRequest;
import org.example.flow.geo.dto.CurrentLocationDtos.CurrentLocationResponse;
import org.example.flow.geo.dto.KakaoLocalDtos.ReverseGeocodeResponse;
import org.example.flow.geo.service.KakaoLocalService;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/location")
@RequiredArgsConstructor
public class LocationController {


    private final KakaoLocalService kakaoLocalService;


    // 프론트에서 현위치 좌표를 POST로 보냄
// 예) POST /api/location/current {"lat":37.592, "lng":127.005}
    @PostMapping("/current")
    public CurrentLocationResponse current(@Valid @RequestBody CurrentLocationRequest req) {
        ReverseGeocodeResponse res = kakaoLocalService.reverseGeocode(req.lat(), req.lng());
        var doc = res.documents().isEmpty() ? null : res.documents().get(0);


        String addressName = doc != null ? (doc.road_address() != null && doc.road_address().address_name() != null
                ? doc.road_address().address_name()
                : doc.address() != null ? doc.address().address_name() : null) : null;


        String r1 = doc != null && doc.address() != null ? doc.address().region_1depth_name() : null;
        String r2 = doc != null && doc.address() != null ? doc.address().region_2depth_name() : null;
        String r3 = doc != null && doc.address() != null ? doc.address().region_3depth_name() : null;
        String road = doc != null && doc.road_address() != null ? doc.road_address().address_name() : null;


        return new CurrentLocationResponse(req.lat(), req.lng(), addressName, r1, r2, r3, road);
    }
}