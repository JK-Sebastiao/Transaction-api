package com.payvyne.transaction.api.config.security;

import com.payvyne.transaction.api.authentication.model.User;
import io.jsonwebtoken.*;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

@Component
@AllArgsConstructor
public class JwtTokenUtil {
    private static final Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);
    private final String JWT_SECRET = "zdtlD3JK56m6wTTgsNFhqzjqP";
    private final String JWT_ISSUER = "payvyne.com";

    public String generateAccessToken(User user){
        return "Bearer "+ Jwts.builder()
                .setSubject(String.format("%s,%s",user.getId(),user.getUsername()))
                .setIssuer(JWT_ISSUER)
                .setIssuedAt(convertFrom(LocalDateTime.now()))
                .setExpiration(convertFrom(LocalDateTime.now().plusMinutes(30)))
                .signWith(SignatureAlgorithm.HS512,JWT_SECRET)
                .compact();
    }

    public String getUserId(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject().split(",")[0];
    }



    public String getUsername(String token){
        Claims claims = getClaims(token);
        return claims.getSubject().split(",")[1];
    }

    public Date getExpirationDate(String token){
        Claims claims = getClaims(token);
        return claims.getExpiration();
    }

    public boolean validate(String token){
        try {
            Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(token);
            return true;
        } catch (SignatureException ex){
            logger.error("Invalid JWT signature - {}", ex.getMessage());
        } catch (MalformedJwtException ex){
            logger.error("Invalid JWT token - {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            logger.error("Expired JWT token - {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            logger.error("Unsupported JWT token - {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            logger.error("JWT claims string is empty - {}", ex.getMessage());
        }
        return false;
    }

    private Claims getClaims(String token) {
        return Jwts.parser()
                .setSigningKey(JWT_SECRET)
                .parseClaimsJws(token)
                .getBody();
    }

    private Date convertFrom(LocalDateTime localDateTime){
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

}
