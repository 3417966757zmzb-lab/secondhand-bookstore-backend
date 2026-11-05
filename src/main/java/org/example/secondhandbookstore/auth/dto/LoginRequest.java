package org.example.secondhandbookstore.auth.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class LoginRequest {

    @NotBlank(message = "邮箱或者用户名不能为空")
    private String account;
    @NotBlank(message = "密码不能为空")
    private String password;

}
