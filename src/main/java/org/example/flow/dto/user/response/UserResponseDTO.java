package org.example.flow.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor

public class UserResponseDTO {
    private String email;
    private String nickname;
//    private String profileImage;
}
