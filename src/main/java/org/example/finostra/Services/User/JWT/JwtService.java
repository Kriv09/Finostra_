package org.example.finostra.Services.User.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class JwtService {

    @Value("${jwt.secret}")      private String secret;
    @Value("${jwt.exp-min}")     private long   expMinutes;
    @Value("${jwt.refresh-days}") private long   refreshDays;

    public String generate(UserDetails user, String publicUuid) {
        return buildToken(user, publicUuid, Duration.ofMinutes(expMinutes));
    }

    public String generateRefresh(UserDetails user, String publicUuid) {
        return buildToken(user, publicUuid, Duration.ofDays(refreshDays));
    }

    public Claims parse(String token)       { return parseInternal(token); }
    public Claims parseRefresh(String tok)  { return parseInternal(tok);   }

    private String buildToken(UserDetails u, String uuid, Duration ttl) {
        Date now = new Date();
        Date exp = Date.from(now.toInstant().plus(ttl));

        return Jwts.builder()
                .setSubject(uuid)
                .claim("usr",   u.getUsername())
                .claim("roles", u.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).toList())
                .setIssuedAt(now)
                .setExpiration(exp)
                .signWith(Keys.hmacShaKeyFor(secret.getBytes()),
                        SignatureAlgorithm.HS256)
                .compact();
    }

    private Claims parseInternal(String tok) {
        return Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secret.getBytes()))
                .build()
                .parseClaimsJws(tok)
                .getBody();
    }
}