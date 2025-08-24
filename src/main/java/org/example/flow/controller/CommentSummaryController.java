package org.example.flow.controller;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.shop.response.ShopInfoResponseDTO;
import org.example.flow.entity.ShopInfo;
import org.example.flow.service.user.ShopInfoService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/comments")
@RequiredArgsConstructor
public class CommentSummaryController {

    private final ShopInfoService shopInfoService;

    // ✅ Google Places 리뷰 기반 코멘트 생성
    @PostMapping("/{shopInfoId}/google")
    public ResponseEntity<ShopInfoResponseDTO> generateCommentsFromGoogle(
            @PathVariable Long shopInfoId,
            @RequestParam String placeId
    ) throws Exception {
        ShopInfo updatedShop = shopInfoService.updateShopInfoWithGoogleReviews(shopInfoId, placeId);
        return ResponseEntity.ok(new ShopInfoResponseDTO(
                updatedShop.getShopInfoId(),
                updatedShop.getComment1(),
                updatedShop.getComment2(),
                updatedShop.getComment3()
        ));
    }
}