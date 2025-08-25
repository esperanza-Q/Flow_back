package org.example.flow.service.shopMypage;

import org.example.flow.dto.shopMypage.request.AcceptPaymentRequest;
import org.example.flow.dto.shopMypage.response.*;
import org.example.flow.service.recommendation.PaymentConfirmService;
import org.example.flow.service.recommendation.RewardService;
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

import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
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
    private final RewardService rewardService;
    private final PaymentConfirmService paymentConfirmService;

    private static final DateTimeFormatter TF = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final DateTimeFormatter TF_SHORT = DateTimeFormatter.ofPattern("HH:mm");

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

        // 1) 영업시간 병합
        List<BusinessHours> hours = businessHoursRepository.findByShopInfo(shopInfo);
        List<UpdateShopInfoResponse.BusinessHoursDto> businessHoursDtos = toBusinessHoursDtos(hours);

        // 2) 이미지
        List<ShopImage> images = shopImageRepository.findByShopInfo(shopInfo);
        List<UpdateShopInfoResponse.ImageDto> imageDtos = images.stream()
                .map(i -> new UpdateShopInfoResponse.ImageDto(String.valueOf(i.getImage())))
                .collect(Collectors.toList());

        // 3) BenefitReq
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
                shopInfo.getExplanationTitle(),      // ⬅️ 제목
                shopInfo.getExplanationContent(),    // ⬅️ 본문
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

        // key: "HH:mm|HH:mm"  (예: "09:00|21:00")
        Map<String, List<BusinessHours.Week>> grouped = new LinkedHashMap<>();

        for (BusinessHours h : hours) {
            // ✅ LocalTime 안전 포맷
            String open  = h.getOpenTime()  != null ? h.getOpenTime().format(TF_SHORT)  : null;
            String close = h.getCloseTime() != null ? h.getCloseTime().format(TF_SHORT) : null;

            String key = open + "|" + close;
            grouped.computeIfAbsent(key, k -> new ArrayList<>()).add(h.getWeek());
        }

        // 요일 정렬 순서
        List<BusinessHours.Week> order = Arrays.asList(
                BusinessHours.Week.MONDAY,
                BusinessHours.Week.TUESDAY,
                BusinessHours.Week.WEDNESDAY,
                BusinessHours.Week.THURSDAY,
                BusinessHours.Week.FRIDAY,
                BusinessHours.Week.SATURDAY,
                BusinessHours.Week.SUNDAY
        );

        // 그룹 → DTO
        return grouped.entrySet().stream()
                .map(e -> {
                    String[] times = e.getKey().split("\\|", 2);
                    String open  = times[0];
                    String close = times[1];

                    String weekJoined = e.getValue().stream()
                            .sorted(Comparator.comparingInt(order::indexOf))
                            .map(this::weekToAbbrev)                 // ✅ MON/TUE/...로 변환
                            .collect(Collectors.joining("/"));

                    return new UpdateShopInfoResponse.BusinessHoursDto(weekJoined, open, close);
                })
                // "09:00" 같은 0패딩이므로 문자열 정렬로도 안전
                .sorted(Comparator.comparing(UpdateShopInfoResponse.BusinessHoursDto::getOpenTime)
                        .thenComparing(UpdateShopInfoResponse.BusinessHoursDto::getCloseTime))
                .collect(Collectors.toList());
    }

    private String weekToAbbrev(BusinessHours.Week w) {
        return switch (w) {
            case MONDAY    -> "MON";
            case TUESDAY   -> "TUE";
            case WEDNESDAY -> "WED";
            case THURSDAY  -> "THU";
            case FRIDAY    -> "FRI";
            case SATURDAY  -> "SAT";
            case SUNDAY    -> "SUN";
        };
    }

    @Transactional
    public UpdateShopInfoResponse updateShopInfo(Long shopInfoId, UpdateShopInfoRequest req) {
        ShopInfo shopInfo = shopInfoRepository.findById(shopInfoId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "ShopInfo not found"));

        // 1) 설명 수정 (제목/본문 분리 반영)
        if (req.getExplanationTitle() != null) {
            shopInfo.setExplanationTitle(req.getExplanationTitle());
        }
        if (req.getExplanationContent() != null) {
            shopInfo.setExplanationContent(req.getExplanationContent());
        }
        shopInfoRepository.save(shopInfo);

        // 2) 영업시간 전체 교체
        if (req.getBusiness_hours() != null) {
            businessHoursRepository.deleteByShopInfo(shopInfo);

            for (UpdateShopInfoRequest.BusinessHoursDto dto : req.getBusiness_hours()) {
                BusinessHours h = new BusinessHours();
                h.setShopInfo(shopInfo);
                h.setWeek(toWeekEnum(dto.getWeek()));
                // "HH:mm" → LocalTime 파싱 (엔티티가 LocalTime 사용한다고 가정)
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

        // 최종 응답
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
    public PaymentCheckResponse acceptPaymentCheck(Long paymentCheckId, AcceptPaymentRequest request) {
        PaymentCheck pc = paymentCheckRepository.findById(paymentCheckId)
                .orElseThrow(() -> new IllegalArgumentException("payment_check not found: id=" + paymentCheckId));

        if (pc.getStatus() == PaymentCheck.STATUS.REJECT) {
            throw new IllegalStateException("Already REJECT. Cannot accept: id=" + paymentCheckId);
        }

        // (선택) amount 반영
        if (request != null && request.getAmount() != null) {
            pc.setAmount(request.getAmount());
        }

        // 🔑 '첫 승인'인지 판단
        boolean justAccepted = (pc.getStatus() != PaymentCheck.STATUS.ACCEPT);

        if (justAccepted) {
            pc.setStatus(PaymentCheck.STATUS.ACCEPT);
            paymentCheckRepository.save(pc);
            paymentCheckRepository.flush(); // 즉시 반영
        }

        Long userId = pc.getUser().getUserId();
        Long shopInfoId = pc.getShopInfo().getShopInfoId();
        long amount = (pc.getAmount() != null) ? pc.getAmount().longValue() : 0L;

        // 🔑 첫 승인 여부를 confirm에 전달
        Map<String, Object> confirm = paymentConfirmService.confirm(
                paymentCheckId, userId, shopInfoId, amount, justAccepted
        );

        boolean matched          = (boolean) confirm.getOrDefault("matched", false);
        boolean counted          = (boolean) confirm.getOrDefault("counted", false);
        int     countThisWeek    = ((Number) confirm.getOrDefault("countThisWeek", 0)).intValue();
        boolean awarded50        = (boolean) confirm.getOrDefault("awarded50", false);
        boolean awarded100       = (boolean) confirm.getOrDefault("awarded100", false);
        Long    nextRecommendId  = (confirm.get("nextRecommendShopId") instanceof Number n) ? n.longValue() : null;
        int     paymentPoints    = ((Number) confirm.getOrDefault("paymentPoints", 0)).intValue();

        return PaymentCheckResponse.builder()
                .paymentCheckId(paymentCheckId)
                .userId(userId)
                .shopInfoId(shopInfoId)
                .amount(amount)
                .status("ACCEPT")
                .createdAt(toUtcString(pc.getCreatedAt()))
                .matched(matched)
                .paymentPoints(paymentPoints)
                .nextRecommendShopId(matched ? nextRecommendId : null)  // next는 여전히 matched 기준
                .counted(counted)                  // ✅ matched=false여도 첫 승인이라면 true
                .countThisWeek(countThisWeek)
                .awarded50(awarded50)              // matched=false 경로에서는 false
                .awarded100(awarded100)            // matched=false 경로에서는 false
                .build();
    }

    private String toUtcString(java.time.LocalDateTime ldt) {
        if (ldt == null) return OffsetDateTime.now(ZoneOffset.UTC).toString();
        return ldt.atOffset(ZoneOffset.UTC).toString();
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
