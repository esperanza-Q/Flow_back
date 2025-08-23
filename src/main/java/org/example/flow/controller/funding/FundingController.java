package org.example.flow.controller.funding;

import lombok.RequiredArgsConstructor;
import org.example.flow.dto.funding.response.FundingDetailResponseDTO;
import org.example.flow.dto.funding.response.FundingResponseDTO;
import org.example.flow.dto.user.response.UserResponseDTO;
import org.example.flow.entity.User;
import org.example.flow.service.funding.FundingService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}
