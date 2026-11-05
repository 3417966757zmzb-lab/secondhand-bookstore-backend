package org.example.secondhandbookstore.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import org.example.secondhandbookstore.user.dto.UserRegisterRequest;
import org.example.secondhandbookstore.user.dto.UserResponse;
import org.example.secondhandbookstore.user.entity.User;

public interface UserService extends IService<User> {

    UserResponse registerUser(UserRegisterRequest userRegisterRequest);
}
