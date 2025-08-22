package org.example.flow.dto.userMypage.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@AllArgsConstructor
public class UserMypageResponse {

    @JsonProperty("user_id")
    private Long userId;

    private String nickname;

    private ProfileDto profile;

    @Getter
    @AllArgsConstructor
    public static class ProfileDto {
        private Long profileId;
        private Integer localConsumption;
        private Integer funding;
        private Integer seeds;
        private Integer point;
    }
}