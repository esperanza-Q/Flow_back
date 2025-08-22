
package org.example.flow.dto.userMypage.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class UseCouponRequest {
    @JsonProperty("shopInfo_id")
    private Long shopInfoId; // VISIT 타입에서만 필수
}


