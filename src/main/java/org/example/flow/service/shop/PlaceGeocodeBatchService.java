package org.example.flow.service.shop;

import lombok.RequiredArgsConstructor;
import org.example.flow.repository.PlaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PlaceGeocodeBatchService {

    private final PlaceRepository placeRepository;
    private final PlaceGeocodeWriter placeGeocodeWriter; // 새 트랜잭션 담당

    /** 컨트롤러에서 쓰는 2-파라미터 버전: afterId=0 기본 */
    @Transactional(readOnly = true)
    public int fillMissing(int pageSize, long throttleMillis) {
        return fillMissingOnce(pageSize, throttleMillis, 0L);
    }

    /** 한 페이지만 처리: afterId 이후부터 pageSize 만큼만 처리 */
    @Transactional(readOnly = true)
    public int fillMissingOnce(int pageSize, long throttleMillis, long afterId) {
        var targets = placeRepository.findTargets(afterId, pageSize);
        int updated = 0;

        for (Object[] row : targets) {
            long id = ((Number) row[0]).longValue();   // placeId
            String name = (String) row[1];             // name
            String location = (String) row[2];         // location

            placeGeocodeWriter.safeUpdateCoords(id, name, location);

            if (throttleMillis > 0) {
                try { Thread.sleep(throttleMillis); } catch (InterruptedException ignored) {}
            }
            updated++;
        }
        return updated;
    }

}
