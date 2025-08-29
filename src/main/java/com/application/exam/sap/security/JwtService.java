package com.application.exam.sap.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Duration;
import java.util.Date;


@Service
public class JwtService {
@Value("${security.jwt.secret}") private String secret;
@Value("${security.jwt.expiration-minutes}") private long expirationMinutes;


private Key key() {
return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
}


public String generateToken(String username, String role) {
Date now = new Date();
Date exp = new Date(now.getTime() + Duration.ofMinutes(expirationMinutes).toMillis());
return Jwts.builder()
.setSubject(username)
.claim("role", role)
.setIssuedAt(now)
.setExpiration(exp)
.signWith(key(), SignatureAlgorithm.HS256)
.compact();
}


public Claims parse(String token) {
return Jwts.parserBuilder().setSigningKey(key()).build().parseClaimsJws(token).getBody();
}
}
