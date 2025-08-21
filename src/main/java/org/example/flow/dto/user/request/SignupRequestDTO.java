package org.example.flow.dto.user.request;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.example.flow.entity.User;

@AllArgsConstructor
@Setter
@Getter
@Data
public class SignupRequestDTO {
    private String nickname;
    private String email;
    private String password;
    private User.Role role;
    private String location;
}
