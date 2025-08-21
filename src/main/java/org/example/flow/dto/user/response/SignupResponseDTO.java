package org.example.flow.dto.user.response;


import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
public class SignupResponseDTO {
    private Long userId;
    private Long shopInfoId;

    public SignupResponseDTO(Long userId, Long shopInfoId) {
        this.userId = userId;
        this.shopInfoId = shopInfoId;
    }
}
