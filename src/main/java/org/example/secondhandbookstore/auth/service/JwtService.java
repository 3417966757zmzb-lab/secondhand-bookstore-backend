package org.example.secondhandbookstore.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.example.secondhandbookstore.auth.config.JwtProperties;
import org.example.secondhandbookstore.user.entity.User;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.UUID;

@Service
public class JwtService {

    private final JwtProperties jwtProperties;
    private final SecretKey signingKey;

    public JwtService(JwtProperties jwtProperties){
        this.jwtProperties = jwtProperties;
        this.signingKey = Keys.hmacShaKeyFor(jwtProperties.getSecret().getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(User user) {
        Instant issuedAt  = Instant.now();
        Instant expiration = issuedAt.plus(jwtProperties.getExpiration());
        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("userId",user.getId())
                .claim("role",user.getRole())
                .setId(UUID.randomUUID().toString())//jti token的id
                .setIssuer(jwtProperties.getIssuer())
                .setIssuedAt(Date.from(issuedAt))//将Instant对象转换成Date对象
                .setExpiration(Date.from(expiration))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public Claims parseToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .requireIssuer(jwtProperties.getIssuer())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

}
