package com.onlinejudge.utils;

import java.security.Key;
import java.util.Date;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtUtils {

    private final Key key;
    private final long expirationMs;

    public JwtUtils(@Value("${jwt.secret}") String secretKey,
                    @Value("${jwt.expirationMs}") long expirationMs) {
        this.key = Keys.hmacShaKeyFor(secretKey.getBytes());
        this.expirationMs = expirationMs;
    }

    /**
     * Sinh JWT token từ id, username và role
     */
    public String generateToken(int id, String username, String role) {
        return Jwts.builder()
                .setSubject(username)   // username lưu vào subject
                .claim("id", id)        // thêm claim id
                .claim("role", role)    // thêm claim role
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expirationMs))
                .signWith(key)
                .compact();
    }

    /**
     * Validate token, ném exception nếu token không hợp lệ
     */
    public Claims validateToken(String token) throws JwtException {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    /**
     * Lấy id từ token
     */
    public Integer getIdFromToken(String token) {
        try {
            return ((Number) validateToken(token).get("id")).intValue();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Lấy username từ token
     */
    public String getUsernameFromToken(String token) {
        try {
            return validateToken(token).getSubject();
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Lấy role từ token
     */
    public String getRoleFromToken(String token) {
        try {
            return (String) validateToken(token).get("role");
        } catch (JwtException e) {
            return null;
        }
    }

    /**
     * Kiểm tra token còn hạn hay không
     */
    public boolean isTokenExpired(String token) {
        try {
            Date expiration = validateToken(token).getExpiration();
            return expiration.before(new Date());
        } catch (JwtException e) {
            return true;
        }
    }

    /**
     * Kiểm tra token hợp lệ và chưa hết hạn
     */
    public boolean isTokenValid(String token) {
        return !isTokenExpired(token) && getUsernameFromToken(token) != null;
    }
}
