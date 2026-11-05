package org.example.secondhandbookstore.auth.service;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.example.secondhandbookstore.auth.config.JwtProperties;
import org.example.secondhandbookstore.auth.dto.LoginRequest;
import org.example.secondhandbookstore.auth.dto.LoginResponse;
import org.example.secondhandbookstore.common.exception.BusinessException;
import org.example.secondhandbookstore.common.exception.TooManyRequestsException;
import org.example.secondhandbookstore.user.entity.User;
import org.example.secondhandbookstore.user.enums.UserStatus;
import org.example.secondhandbookstore.user.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;
import java.util.Locale;

import static org.example.secondhandbookstore.common.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final JwtProperties jwtProperties;
    private final LoginAttemptService loginAttemptService;
    private final JwtBlacklistService jwtBlacklistService;

    private static final  String PREFIX = "Bearer ";


    public LoginResponse login(LoginRequest loginRequest , String clientIp) {
        //获取登录的邮箱或者用户名  去除首尾空白并大转小
        String account = loginRequest.getAccount().trim().toLowerCase(Locale.ROOT);

        //如果有等待时间说明登录失败了，直接返回剩余的所需等待时间，没有等待时间就跳过这个步骤;
        long retryAfterSeconds = loginAttemptService.getRetryAfterSeconds(account,clientIp);
        if(retryAfterSeconds > 0) {
            throw new TooManyRequestsException(TOO_MANY_LOGIN_ATTEMPTS, retryAfterSeconds);
        }


        User user =  userService.getOne(new LambdaQueryWrapper<User>()
                .eq(User::getUsername,account)
                .or()
                .eq(User::getEmail,account));
        //没查到用户 状态禁用 密码不对 返回认证失败(登录失败)
        if(user == null || user.getStatus() != UserStatus.ENABLED
           || !passwordEncoder.matches(loginRequest.getPassword(),user.getPasswordHash())){
            //创建失败计数key和重试剩余等待时间key
            loginAttemptService.saveLoginFailure(account,clientIp);
            throw new BusinessException(INVALID_CREDENTIALS);
        }

        //登录成功删除失败计数key和重试剩余等待时间key
        loginAttemptService.removeLoginFailures(account,clientIp);

        LoginResponse loginResponse = new LoginResponse();
        //生成jwt
        loginResponse.setAccessToken(jwtService.generateToken(user));
        loginResponse.setTokenType("Bearer");
        loginResponse.setExpiresIn(jwtProperties.getExpiration().toSeconds());//转成秒
        loginResponse.setUserId(user.getId());
        loginResponse.setUsername(user.getUsername());
        loginResponse.setNickname(user.getNickname());
        loginResponse.setRole(user.getRole());

        return loginResponse;
    }

    public void logout(String authorizationHeader){
        if(authorizationHeader == null || !authorizationHeader.startsWith(PREFIX)){
            return;
        }
        Claims claims;
        String token = authorizationHeader.substring(PREFIX.length()).trim();
        try {
            claims = jwtService.parseToken(token);
        }catch(JwtException e){
            return;
        }

        String jti = claims.getId();
        Date expiration = claims.getExpiration();
        if(jti == null || expiration == null){
            return;
        }
        //剩余时间戳
        long remainMillis= expiration.getTime()-System.currentTimeMillis();
        if(remainMillis <= 0){
            return;
        }
        //加入黑名单
        jwtBlacklistService.addToBlacklist(jti,Duration.ofMillis(remainMillis));

    }

}
