package com.example.scheduler.jwt;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JWTUtil {

    private SecretKey secretKey;
    private JWTUtil(@Value("${spring.jwt.secret}")String secret) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
    }

    public String getUsername(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("username", String.class);
        } catch (ExpiredJwtException ex) {
            return ex.getClaims().get("username").toString();
        } catch (JwtException ex) {
            // JWT 파싱 실패 등의 다른 예외 발생 시
            return "fail";
        }
    }

    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role",String.class);
    }

    public String getRandomKey(String token) {
        try {
            return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("randomkey", String.class);
        } catch (ExpiredJwtException ex) {
            return ex.getClaims().get("randomkey").toString();
        } catch (JwtException ex) {
            // JWT 파싱 실패 등의 다른 예외 발생 시
            return "fail";
        }
    }

    public Boolean isExpired(String token) {
//        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
        try {
            Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
            // JWT가 유효할 경우
            return false; // JWT가 만료되지 않음
        } catch (ExpiredJwtException ex) {
            // JWT가 만료된 경우
            return true; // JWT가 만료됨
        } catch (JwtException ex) {
            // JWT 파싱 실패 등의 다른 예외 발생 시
            return true; // JWT가 만료된 것으로 처리
        }
    }

    public String createJwt(String username, String role, String randomkey, Long expiredMs) {

        return Jwts.builder()
                .claim("username", username)
                .claim("role", role)
                .claim("randomkey",randomkey)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

    public String createJWT(String username, String role, Long expiredMs) {

        return Jwts.builder()
                .claim("username",username)
                .claim("role", role)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();

    }

}
