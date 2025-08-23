package org.example.flow.controller.funding;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.funding.request.SeedGiveRequestDTO;
import org.example.flow.dto.funding.response.*;
import org.example.flow.dto.user.response.UserResponseDTO;
import org.example.flow.entity.User;
import org.example.flow.service.funding.FundingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value = "/api/funding")
@RequiredArgsConstructor
public class FundingController {

    private final FundingService fundingService;

    @GetMapping("")
    public ResponseEntity<?> getFunding() {
        FundingResponseDTO response = fundingService.getFunding();
        return ResponseEntity.ok(response);
    }

    @GetMapping("/detail")
    public ResponseEntity<?> getFundingDetail(@RequestParam Long fundingId) {
        FundingDetailResponseDTO response = fundingService.getFundingDetail(fundingId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/seeds")
    public ResponseEntity<?> getSeedPopup(@RequestParam Long fundingId) {
        SeedPopupResponseDTO response = fundingService.getSeedPopup(fundingId);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/giveSeeds")
    public ResponseEntity<?> giveSeed(@RequestBody SeedGiveRequestDTO seedGiveRequestDTO) {
        SeedGiveResponseDTO responseDTO = fundingService.giveSeed(seedGiveRequestDTO);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("/myfunding")
    public ResponseEntity<?> getMyFunding() {
        List<MyFundingResponseDTO> responseDTO = fundingService.getMyFunding();
        return ResponseEntity.ok(responseDTO);
    }

}
