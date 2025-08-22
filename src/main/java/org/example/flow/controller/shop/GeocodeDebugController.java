package org.example.flow.controller.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.service.shop.GeocodingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/debug")
public class GeocodeDebugController {

    private final GeocodingService geocodingService;

    @GetMapping("/geocode") // → /api/debug/geocode
    public Map<String,Object> geocode(@RequestParam("q") String query) {
        double[] xy = geocodingService.geocode(null, query);
        return Map.of("query", query, "lat", xy != null ? xy[0] : null, "lng", xy != null ? xy[1] : null);
    }
}
