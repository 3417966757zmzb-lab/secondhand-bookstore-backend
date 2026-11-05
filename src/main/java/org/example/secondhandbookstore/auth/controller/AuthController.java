package org.example.secondhandbookstore.auth.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.auth.dto.LoginRequest;
import org.example.secondhandbookstore.auth.dto.LoginResponse;
import org.example.secondhandbookstore.auth.service.AuthService;
import org.example.secondhandbookstore.auth.service.JwtService;
import org.example.secondhandbookstore.common.result.Result;
import org.example.secondhandbookstore.common.security.ClientIpResolver;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;
    private final ClientIpResolver clientIpResolver;


    @PostMapping("/login")
    public Result<LoginResponse> loginUser(@Valid @RequestBody LoginRequest loginRequest, HttpServletRequest request) {
        return Result.success(authService.login(loginRequest,clientIpResolver.resolve(request)));
    }

    @PostMapping("/logout")
    public Result<String> logout(@RequestHeader(HttpHeaders.AUTHORIZATION)String authorizationHeader){
         authService.logout(authorizationHeader);
         return Result.success("登出成功");

    }
 }
