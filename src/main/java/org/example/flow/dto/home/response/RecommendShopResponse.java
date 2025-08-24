package org.example.flow.dto.home.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendShopResponse {

    private String nickname;
    private List<String> checkDate;
    private String shopName;
    private Boolean openStatus;
    private Long shopInfoId;
    private String shopImage;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Comment {
        private String comment;
    }
    private List<Comment> comments;

    private String recommendInfo;
    private Integer visitCount;
    private String couponType;
}
