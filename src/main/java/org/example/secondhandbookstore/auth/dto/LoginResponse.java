package org.example.secondhandbookstore.auth.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.secondhandbookstore.user.enums.UserRole;

@Data
@NoArgsConstructor
public class LoginResponse {
    //访问令牌
    private String accessToken;
    //令牌类型
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String username;
    private String nickname;
    private UserRole role;

}
