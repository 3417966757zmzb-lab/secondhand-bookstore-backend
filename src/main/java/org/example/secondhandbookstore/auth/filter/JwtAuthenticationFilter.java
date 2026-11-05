package org.example.secondhandbookstore.auth.filter;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.secondhandbookstore.auth.service.JwtBlacklistService;
import org.example.secondhandbookstore.auth.service.JwtService;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component //把被标注的类标记为 Spring 管理的组件，Spring 启动时通过组件扫描自动把它注册成 Bean
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    private final JwtService jwtService;
    private final JwtBlacklistService jwtBlacklistService;


    private static final  String HEADER = "Authorization";
    private static final  String PREFIX = "Bearer ";
    private static final String ROLE_PREFIX = "ROLE_";

    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {

        String token = resolveToken(request);
        if(token == null){
            filterChain.doFilter(request, response);
            return;
        }

        try{
            Claims claims = jwtService.parseToken(token);
            String jti = claims.getId();

            if(jti == null || jwtBlacklistService.isBlacklisted(jti)){
                filterChain.doFilter(request, response);
                return;
            }
            UsernamePasswordAuthenticationToken authentication = buildAuthentication(claims);
            if(authentication != null) {
                //把当前用户的认证对象 Authentication 放进当前线程的 Spring Security 上下文，让后续的权限判断、获取当前用户等操作能正常工作
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }

        }catch(JwtException e){
            log.debug("jwt令牌校验失败, {} ",e.getMessage());
        }

        filterChain.doFilter(request, response);

    }


    //从头部取token
    private String resolveToken(HttpServletRequest request) {
        String header = request.getHeader(HEADER);
        if(header == null || !header.startsWith(PREFIX)){
            return null;
        }
        String token = header.substring(PREFIX.length()).trim();
        return token.isBlank() ? null :token;
    }

    private UsernamePasswordAuthenticationToken buildAuthentication(Claims claims) {
        Number userId = claims.get("userId",Number.class);
        if(userId == null){
            return null;
        }
        String role = claims.get("role",String.class);
        List<SimpleGrantedAuthority> authorities = role == null ?
                List.of() : List.of(new SimpleGrantedAuthority(ROLE_PREFIX+role));
        //参数位置：1.当前用户是谁 2.密码或凭证 3.用户权限
        return new UsernamePasswordAuthenticationToken(userId.longValue(), null, authorities);
    }

}
