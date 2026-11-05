package org.example.secondhandbookstore.user.controller;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.common.result.Result;
import org.example.secondhandbookstore.user.dto.UserRegisterRequest;
import org.example.secondhandbookstore.user.dto.UserResponse;
import org.example.secondhandbookstore.user.service.UserService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping("/register")
    public Result<UserResponse> registerUser(@Valid @RequestBody UserRegisterRequest userRegisterRequest){
        return Result.success(userService.registerUser(userRegisterRequest));
    }



}
