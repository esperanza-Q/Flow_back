package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.entity.Place;
import org.example.flow.entity.SeongbukPlace;
import org.example.flow.repository.PlaceRepository;
import org.example.flow.repository.SeongbukPlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

// PlaceImportService.java
@Service
@RequiredArgsConstructor
public class PlaceImportService {

    private final SeongbukPlaceRepository seongbukRepo;
    private final PlaceRepository placeRepo;

    /**
     * 성북구 수집데이터를 Place로 1회성 이관
     * - shopInfo: null 로 둠 (추후 상점가입시 1:1 연결)
     * - category: 한글 → enum 변환
     * - latitude/longitude: 아직 없으므로 null
     */
    @Transactional
    public int importFromSeongbuk() {
        List<SeongbukPlace> src = seongbukRepo.findAll();
        List<Place> toSave = new ArrayList<>(src.size());

        for (SeongbukPlace s : src) {
            Place.Category cat = Place.Category.fromKorean(s.getCategory());

            Place p = Place.builder()
                    .shopInfo(null)
                    .name(s.getName())
                    .location(s.getAddress())
                    .category(cat)
                    .latitude(null)
                    .longitude(null)
                    .build();

            toSave.add(p);
        }

        placeRepo.saveAll(toSave);
        return toSave.size();
    }
}
