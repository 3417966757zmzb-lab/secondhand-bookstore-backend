package org.example.secondhandbookstore.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserRegisterRequest {

    @NotBlank(message = "用户名不能为空")
    @Size(min = 4 , max = 20 , message = "用户名在4-20字符之间")
    @Pattern(regexp = "^[a-zA-Z0-9_]+$", message = "只允许字母,数字,下划线")
    private String username;

    @NotBlank(message = "密码不能为空")
    @Size(min = 8 , max = 30 , message = "密码长度在8-30之间")
    @Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d).+$" , message = "必须包含数字和字母")
    private String password;

    @NotBlank(message = "昵称不能为空")
    @Size(max = 20 , message = "昵称最大长度为20")
    private String nickname;

    @Pattern(regexp =  "^$|^1[3-9]\\d{9}$" , message = "手机号格式不正确")
    private String phone;

    @NotBlank(message = "邮箱不能为空")
    @Email
    @Size(max = 100)
    private String email;
}
