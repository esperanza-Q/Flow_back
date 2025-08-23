package org.example.flow.service.shopMypage;

import org.example.flow.dto.shopMypage.request.AcceptPaymentRequest;
import org.example.flow.dto.shopMypage.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shopMypage.request.UpdateShopInfoRequest;
import org.example.flow.entity.*;
import org.springframework.data.domain.Sort;
import org.example.flow.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.multipart.MultipartFile;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.NOT_FOUND;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

@Service
@RequiredArgsConstructor
public class ShopMypageService {

    private final ShopInfoRepository shopInfoRepository;
    private final BusinessHoursRepository businessHoursRepository;
    private final ShopImageRepository shopImageRepository;
    private final BenefitReqRepository benefitReqRepository;
    private final PaymentCheckRepository paymentCheckRepository;
    private final RewardCouponRepository rewardCouponRepository;

    private static final Path UPLOAD_DIR = Path.of("uploads", "reward-coupon"); // 경로 변경


    public SummaryShopInfoResponse getSummaryShopInfo(Long shopInfoId) {
        ShopInfo shopInfo = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "ShopInfo not found"));

        // nowMonth -> "2025-08" 변환
        int nowMonthInt = shopInfo.getNowMonth(); // 202508
        String yearMonthStr = String.valueOf(nowMonthInt); // "202508"
        YearMonth yearMonth = YearMonth.parse(yearMonthStr, DateTimeFormatter.ofPattern("yyyyMM"));

        String month = yearMonth.format(DateTimeFormatter.ofPattern("yyyy-MM"));
        String from = yearMonth.atDay(1).toString();
        String to = yearMonth.atEndOfMonth().toString();

        return new SummaryShopInfoResponse(
                String.valueOf(shopInfo.getShopInfoId()),
                new SummaryShopInfoResponse.NowMonth(month, from, to),
                shopInfo.getMonthPayment(),
                shopInfo.getPartnershipCost()
        );


    }


    public UpdateShopInfoResponse getUpdateShopInfo(Long shopInfoId) {
        ShopInfo shopInfo = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new RuntimeException("ShopInfo not found"));

        // 1) 영업시간: 동일한 (open, close) 묶음으로 요일 병합
        List<BusinessHours> hours = businessHoursRepository.findByShopInfo(shopInfo);
        List<UpdateShopInfoResponse.BusinessHoursDto> businessHoursDtos = toBusinessHoursDtos(hours);

        // 2) 이미지
        List<ShopImage> images = shopImageRepository.findByShopInfo(shopInfo);
        List<UpdateShopInfoResponse.ImageDto> imageDtos = images.stream()
                .map(i -> new UpdateShopInfoResponse.ImageDto(
                        // ShopImage.image 타입이 Long이면 URL이 아닌 식별자일 수 있음.
                        // URL을 DB에 저장하려면 컬럼 타입을 String으로 바꾸는 걸 권장.
                        String.valueOf(i.getImage())
                ))
                .collect(Collectors.toList());

        // 3) BenefitReq: 씨앗/쿠폰 조건
        Optional<BenefitReq> seedOpt = benefitReqRepository
                .findFirstByShopInfoAndReqNameOrderByBenefitReqIdDesc(shopInfo, BenefitReq.ReqName.SEED);

        Optional<BenefitReq> couponOpt = benefitReqRepository
                .findFirstByShopInfoAndReqNameOrderByBenefitReqIdDesc(shopInfo, BenefitReq.ReqName.COUPON);

        boolean seedCondition   = seedOpt.isPresent();
        String  seedDetail      = seedOpt.map(BenefitReq::getSeedDetail).orElse(null);

        boolean couponCondition = couponOpt.isPresent();
        Integer visitCount      = couponOpt.map(BenefitReq::getVisitCount).orElse(null);
        String  couponType      = couponOpt.map(BenefitReq::getCouponType).orElse(null);
        String  couponImage     = couponOpt.map(BenefitReq::getCouponImage).orElse(null);

        return new UpdateShopInfoResponse(
                businessHoursDtos,
                //타이틀 + 컨텐츠도 추가해야 함.‼️‼️‼️‼️‼️
                shopInfo.getExplanationTitle(),
                imageDtos,
                seedCondition,
                seedDetail,
                couponCondition,
                visitCount,
                couponType,
                couponImage
        );
    }

    /**
     * 동일한 (open, close) 시간대 그룹으로 요일을 병합해서
     * "MON/TUE/WED/..." 문자열을 구성한다.
     */
    private List<UpdateShopInfoResponse.BusinessHoursDto> toBusinessHoursDtos(List<BusinessHours> hours) {
        if (hours == null || hours.isEmpty()) return Collections.emptyList();

        // 시간 포맷터 (BusinessHours에 Date 사용 중)
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

        // key: "HH:mm|HH:mm", value: 요일 목록
        Map<String, List<BusinessHours.Week>> grouped = new HashMap<>();

        for (BusinessHours h : hours) {
            String key = sdf.format(h.getOpenTime()) + "|" + sdf.format(h.getCloseTime());
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(h.getWeek());
        }

        // 요일 정렬 순서 지정
        List<BusinessHours.Week> order = Arrays.asList(
                BusinessHours.Week.MONDAY,
                BusinessHours.Week.TUESDAY,
                BusinessHours.Week.WEDNESDAY,
                BusinessHours.Week.THURSDAY,
                BusinessHours.Week.FRIDAY,
                BusinessHours.Week.SATURDAY,
                BusinessHours.Week.SUNDAY
        );

        // 요일 축약표기
        Map<BusinessHours.Week, String> abbrev = Map.of(
                BusinessHours.Week.MONDAY,    "MON",
                BusinessHours.Week.TUESDAY,   "TUE",
                BusinessHours.Week.WEDNESDAY, "WED",
                BusinessHours.Week.THURSDAY,  "THU",
                BusinessHours.Week.FRIDAY,    "FRI",
                BusinessHours.Week.SATURDAY,  "SAT",
                BusinessHours.Week.SUNDAY,    "SUN"
        );

        // 그룹을 DTO로 변환
        return grouped.entrySet().stream().map(e -> {
                    String[] times = e.getKey().split("\\|", 2);
                    String open = times[0];
                    String close = times[1];

                    // 지정한 요일 순서대로 정렬 후 "MON/TUE/..." join
                    String weekJoined = e.getValue().stream()
                            .sorted(Comparator.comparingInt(order::indexOf))
                            .map(abbrev::get)
                            .collect(Collectors.joining("/"));

                    return new UpdateShopInfoResponse.BusinessHoursDto(weekJoined, open, close);
                }).sorted(Comparator.comparing(UpdateShopInfoResponse.BusinessHoursDto::getOpenTime)
                        .thenComparing(UpdateShopInfoResponse.BusinessHoursDto::getCloseTime))
                .collect(Collectors.toList());
    }

    @Transactional
    public UpdateShopInfoResponse updateShopInfo(Long shopInfoId, UpdateShopInfoRequest req) {
        ShopInfo shopInfo = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "ShopInfo not found"));

        // 1) 설명 수정 ‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
        if (req.getExplanation() != null) {
            shopInfo.setExplanationTitle(req.getExplanation());
            shopInfoRepository.save(shopInfo);
        }

        // 2) 영업시간 전체 교체
        if (req.getBusiness_hours() != null) {
            businessHoursRepository.deleteByShopInfo(shopInfo);

            for (UpdateShopInfoRequest.BusinessHoursDto dto : req.getBusiness_hours()) {
                BusinessHours h = new BusinessHours();
                h.setShopInfo(shopInfo);
                h.setWeek(toWeekEnum(dto.getWeek()));
                //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
                h.setOpenTime(dto.getOpenTime());
                h.setCloseTime(dto.getCloseTime());
                businessHoursRepository.save(h);
            }
        }

        // 3) 이미지 전체 교체
        if (req.getImages() != null) {
            shopImageRepository.deleteByShopInfo(shopInfo);

            for (UpdateShopInfoRequest.ImageDto dto : req.getImages()) {
                ShopImage img = new ShopImage();
                img.setShopInfo(shopInfo);
                img.setImage(dto.getImage());
                shopImageRepository.save(img);
            }
        }

        // 4) BenefitReq (Seed)
        if (req.getSeedCondition() != null) {
            benefitReqRepository.deleteByShopInfoAndReqName(shopInfo, BenefitReq.ReqName.SEED);
            if (req.getSeedCondition()) {
                BenefitReq seed = new BenefitReq();
                seed.setShopInfo(shopInfo);
                seed.setReqName(BenefitReq.ReqName.SEED);
                seed.setSeedDetail(req.getSeedDetail());
                benefitReqRepository.save(seed);
            }
        }

        // 5) BenefitReq (Coupon)
        if (req.getCouponCondition() != null) {
            benefitReqRepository.deleteByShopInfoAndReqName(shopInfo, BenefitReq.ReqName.COUPON);
            if (req.getCouponCondition()) {
                if (req.getVisitCount() == null || req.getVisitCount() < 1)
                    throw new ResponseStatusException(BAD_REQUEST, "visitCount is required and >= 1");

                BenefitReq coupon = new BenefitReq();
                coupon.setShopInfo(shopInfo);
                coupon.setReqName(BenefitReq.ReqName.COUPON);
                coupon.setVisitCount(req.getVisitCount());
                coupon.setCouponType(req.getCouponType());
                coupon.setCouponImage(req.getCouponImage());
                benefitReqRepository.save(coupon);
            }
        }

        // 최종 응답 재활용
        return getUpdateShopInfo(shopInfoId);
    }

    // 요일 변환
    private BusinessHours.Week toWeekEnum(String week) {
        return switch (week.toUpperCase()) {
            case "MON" -> BusinessHours.Week.MONDAY;
            case "TUE" -> BusinessHours.Week.TUESDAY;
            case "WED" -> BusinessHours.Week.WEDNESDAY;
            case "THU" -> BusinessHours.Week.THURSDAY;
            case "FRI" -> BusinessHours.Week.FRIDAY;
            case "SAT" -> BusinessHours.Week.SATURDAY;
            case "SUN" -> BusinessHours.Week.SUNDAY;
            default -> throw new ResponseStatusException(BAD_REQUEST, "Invalid week: " + week);
        };
    }

    //‼️‼️‼️‼️여기 수정 필요‼️‼️‼️‼️
    // "09:00" -> Date 변환
//    private LocalTime parseTime(String hhmm) {
//        try {
//            return new SimpleDateFormat("HH:mm").parse(hhmm);
//        } catch (Exception e) {
//            throw new ResponseStatusException(BAD_REQUEST, "Invalid time format: " + hhmm);
//        }
//    }

    //결제 요청 리스트 조회
    public PaymentCheckListResponse getPaymentChecks(Long shopInfoId, String status, String sortParam) {
        if (shopInfoId == null) {
            throw new ResponseStatusException(BAD_REQUEST, "shopInfoId is required");
        }
        if (status == null || status.isBlank()) {
            throw new ResponseStatusException(BAD_REQUEST, "status is required");
        }

        PaymentCheck.STATUS st;
        try {
            st = PaymentCheck.STATUS.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new ResponseStatusException(BAD_REQUEST, "Invalid status: " + status);
        }

        Sort sort = parseSort(sortParam); // "createdAt,desc" 같은 문자열 파싱

        List<PaymentCheck> list = paymentCheckRepository
                .findByShopInfo_ShopInfoIdAndStatus(shopInfoId, st, sort);

        List<PaymentCheckListResponse.Item> items = list.stream().map(pc -> {
            PaymentCheckListResponse.Item dto = new PaymentCheckListResponse.Item();
            dto.setPaymentCheckId(pc.getPaymentCheckId());
            dto.setUserId(pc.getUser() != null ? pc.getUser().getUserId() : null);
            dto.setShopInfoId(pc.getShopInfo() != null ? pc.getShopInfo().getShopInfoId() : null);
            dto.setAmount(pc.getAmount());
            dto.setStatus(pc.getStatus().name());
            dto.setCreatedAt(pc.getCreatedAt());
            return dto;
        }).toList();

        return new PaymentCheckListResponse(items);
    }

    // "createdAt,desc" | "createdAt,asc" | "createdAt" (기본 asc) 지원
    private Sort parseSort(String sortParam) {
        String defaultField = "createdAt";
        Sort.Direction defaultDir = Sort.Direction.DESC;

        if (sortParam == null || sortParam.isBlank()) {
            return Sort.by(defaultDir, defaultField);
        }

        // 다중 정렬도 지원: "createdAt,desc;paymentCheckId,asc"
        Sort sort = Sort.unsorted();
        String[] parts = sortParam.split(";");
        for (String p : parts) {
            String[] kv = p.split(",", 2);
            String field = kv[0].trim();
            Sort.Direction dir = (kv.length > 1 && "desc".equalsIgnoreCase(kv[1].trim()))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;

            sort = sort.and(Sort.by(dir, field));
        }
        if (sort.isUnsorted()) {
            sort = Sort.by(defaultDir, defaultField);
        }
        return sort;
    }


    //결제 요청 승인
    @Transactional
    public PaymentCheckResponse acceptPaymentCheck(Long paymentCheckId, AcceptPaymentRequest req) {
        if (req == null || req.getAmount() == null || req.getAmount() <= 0) {
            throw new ResponseStatusException(BAD_REQUEST, "amount must be a positive integer");
        }

        PaymentCheck pc = paymentCheckRepository.findById(paymentCheckId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "PaymentCheck not found"));

        // 상태 검증: WAITING만 허용
        if (pc.getStatus() != PaymentCheck.STATUS.WAITING) {
            throw new ResponseStatusException(BAD_REQUEST, "Only WAITING can be accepted");
        }

        // 금액 & 상태 갱신
        pc.setAmount(req.getAmount());
        pc.setStatus(PaymentCheck.STATUS.ACCEPT);
        paymentCheckRepository.save(pc); // 변경감지여도 save 해도 무방

        return new PaymentCheckResponse(
                pc.getPaymentCheckId(),
                pc.getUser() != null ? pc.getUser().getUserId() : null,
                pc.getShopInfo() != null ? pc.getShopInfo().getShopInfoId() : null,
                pc.getAmount(),
                pc.getStatus().name(),
                pc.getCreatedAt()
        );
    }


    //쿠폰 등록하기
    @Transactional
    public RewardCouponResponse addRewardCoupon(Long shopInfoId, String name, Integer amount, MultipartFile image) {
        // 1) 입력값 검증
        if (shopInfoId == null) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "shopInfoId is required");
        if (name == null || name.isBlank()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        if (amount == null || amount <= 0) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be positive");
        if (image == null || image.isEmpty()) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "image file is required");

        // 2) ShopInfo 조회
        ShopInfo shopInfo = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "ShopInfo not found"));

        // 3) (임시 image 값) 엔티티 저장하여 ID 발급
        RewardCoupon coupon = new RewardCoupon();
        coupon.setShopInfo(shopInfo);
        coupon.setName(name);
        coupon.setAmount(amount);
        coupon.setImage(""); // 일단 비워두고 저장
        rewardCouponRepository.saveAndFlush(coupon); // ID 확보 위해 flush

        // 4) 파일명 생성: {id}.{ext}
        String original = image.getOriginalFilename();
        String ext = extractExtensionSafe(original); // 없으면 빈문자열
        String filename = coupon.getRewardCouponId() + (ext.isEmpty() ? "" : "." + ext);

        // 5) 파일 저장
        try {
            if (!Files.exists(UPLOAD_DIR)) {
                Files.createDirectories(UPLOAD_DIR);
            }
            Path target = UPLOAD_DIR.resolve(filename);
            Files.copy(image.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to store file");
        }

        // 6) 파일명 갱신 후 재저장
        coupon.setImage(filename);
        rewardCouponRepository.save(coupon);

        // 7) 응답
        return new RewardCouponResponse(
                coupon.getRewardCouponId(),
                shopInfo.getShopInfoId(),
                coupon.getName(),
                coupon.getAmount(),
                coupon.getImage()
        );
    }

    private String extractExtensionSafe(String filename) {
        if (filename == null) return "";
        int idx = filename.lastIndexOf('.');
        if (idx < 0 || idx == filename.length() - 1) return "";
        return filename.substring(idx + 1).toLowerCase();
    }
}
