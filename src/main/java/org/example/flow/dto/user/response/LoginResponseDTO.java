package org.example.flow.dto.user.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.flow.entity.User;

@Setter
@Getter
@Data
public class LoginResponseDTO {
    private Long userId;
    private User.Role role;
    private String token;

    public LoginResponseDTO(Long userId, User.Role role, String token) {
        this.userId = userId;
        this.role = role;
        this.token = token;
    }
}
