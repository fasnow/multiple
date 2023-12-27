package org.fasnow.mutiple.app;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Date;


public class Jwt {
    private static final String DEFAULT_KEY = "SecretKey012345678901234567890123456789012345678901234567890123456789";

    // 生成 JWT
    public static String generateJwt(String subject) {
        Date expirationDate = new Date(System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000); // 30天后过期

        return Jwts.builder()
                .setSubject(subject)
                .setExpiration(expirationDate)
                .setIssuedAt(null)
                .signWith(SignatureAlgorithm.HS256, DEFAULT_KEY)
                .compact();
    }

    // 验证 JWT
    public static boolean validateJwt(String jwt) {
        try {
            Claims claims = Jwts.parserBuilder().setSigningKey(DEFAULT_KEY).build().parseClaimsJws(jwt).getBody();
            Date expirationDate = claims.getExpiration();
            return !expirationDate.before(new Date()); // 检查是否过期
        } catch (Exception e) {
            return false;
        }
    }

}
