package org.example.flow.controller.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.service.shop.PlaceGeocodeBatchService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.example.flow.service.shop.*;

import java.util.Map;

@RestController
@RequestMapping("/api/shop/place")
@RequiredArgsConstructor
public class PlaceAdminController {

    private final PlaceGeocodeBatchService batchService;

    @PostMapping("/_geocode")
    public Map<String, Object> geocode(@RequestParam(defaultValue = "200") int pageSize,
                                       @RequestParam(defaultValue = "0") long throttle) {
        int updated = batchService.fillMissing(pageSize, throttle);
        return Map.of("updated", updated);
    }
}

