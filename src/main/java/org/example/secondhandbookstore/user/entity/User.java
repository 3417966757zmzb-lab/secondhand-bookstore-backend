package org.example.secondhandbookstore.user.entity;


import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.secondhandbookstore.user.enums.UserRole;
import org.example.secondhandbookstore.user.enums.UserStatus;

import java.time.LocalDateTime;

@Data
@TableName("bs_user")
@NoArgsConstructor
public class User {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String username;
    private String passwordHash;
    private String nickname;
    private String phone;
    private String email;
    //枚举状态
    private UserStatus status;
    //枚举角色权限
    private UserRole role;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
