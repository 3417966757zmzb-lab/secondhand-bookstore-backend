package org.example.secondhandbookstore.user.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.common.exception.BusinessException;
import org.example.secondhandbookstore.user.dto.UserRegisterRequest;
import org.example.secondhandbookstore.user.dto.UserResponse;
import org.example.secondhandbookstore.user.entity.User;
import org.example.secondhandbookstore.user.mapper.UserMapper;
import org.example.secondhandbookstore.user.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Locale;

import static org.example.secondhandbookstore.common.exception.ErrorCode.EMAIL_ALREADY_EXISTS;
import static org.example.secondhandbookstore.common.exception.ErrorCode.USERNAME_ALREADY_EXISTS;
import static org.example.secondhandbookstore.user.enums.UserRole.USER;
import static org.example.secondhandbookstore.user.enums.UserStatus.ENABLED;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    private final PasswordEncoder passwordEncoder;



    @Override

    //注册
    public UserResponse registerUser(UserRegisterRequest userRegisterRequest){

        String phone = userRegisterRequest.getPhone();

        //获取去除首尾空格后的username并转小写
        String username = userRegisterRequest.getUsername().trim().toLowerCase(Locale.ROOT);//转小写
        //判断该用户名是否已经被使用
        boolean usernameExists = this.exists((new LambdaQueryWrapper<User>()
                .eq(User::getUsername,username)));
        if(usernameExists)
            throw new BusinessException(USERNAME_ALREADY_EXISTS);


        String email = userRegisterRequest.getEmail().trim().toLowerCase(Locale.ROOT);
        //判断邮箱是否被使用
        boolean emailExists = this.exists(new LambdaQueryWrapper<User>().eq(User::getEmail,email));
        if(emailExists)
            throw new BusinessException(EMAIL_ALREADY_EXISTS);

        //对密码进行不可逆的哈希加密
        String passwordHash = passwordEncoder.encode(userRegisterRequest.getPassword());



        User user = new User();
        user.setNickname(userRegisterRequest.getNickname().trim());
        user.setUsername(username);
        user.setPasswordHash(passwordHash);
        user.setEmail(email);
        user.setRole(USER);
        user.setPhone(this.normalizePhone(phone));
        user.setStatus(ENABLED);

        this.save(user);
        //获取一个新的user对象
        User savedUser = this.getById(user.getId());
        return this.convertUserToResponse(savedUser);
    }

    private String normalizePhone(String phone) {
        if (phone == null) {
            return null;
        }

        //去除首尾空格后判断是否为空字符串
        String normalizedPhone = phone.trim();
        if (normalizedPhone.isEmpty()) {
            return null;
        }

        return normalizedPhone;
    }

    private UserResponse convertUserToResponse(User user){
        UserResponse userResponse = new UserResponse();
        //将对应字段copy到响应对象里面,当前不包含密码
        BeanUtils.copyProperties(user,userResponse);
        return userResponse;
    }

}
