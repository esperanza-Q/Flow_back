package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.flow.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlaceGeocodeWriter {

    private final PlaceRepository placeRepository;
    private final GeocodeClient geocodeClient; // RestTemplate로 구현된 KakaoGeocodeClient

    /** 각 건은 새 트랜잭션으로 격리 */
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void safeUpdateCoords(long placeId, String name, String location) {
        var geo = geocodeClient.lookup(name, location);
        if (geo == null) {
            log.debug("Geocode miss. id={}, name={}, location={}", placeId, name, location);
            return;
        }
        int cnt = placeRepository.updateCoordsIfNull(placeId, geo.lat(), geo.lng());
        log.debug("Updated coords. id={}, affected={}", placeId, cnt);
        // 별도 flush() 불필요: 커밋 시 자동 flush
    }
}
