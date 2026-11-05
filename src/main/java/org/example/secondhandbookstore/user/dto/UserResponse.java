package org.example.secondhandbookstore.user.dto;


import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.secondhandbookstore.user.enums.UserRole;
import org.example.secondhandbookstore.user.enums.UserStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UserResponse {

    private Long id;
    private String username;
    private String nickname;
    private String phone;
    private String email;
    private UserStatus status;
    private UserRole role;
    private LocalDateTime createdAt;
}
