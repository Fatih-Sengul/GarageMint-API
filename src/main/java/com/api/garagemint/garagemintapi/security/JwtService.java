package com.api.garagemint.garagemintapi.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {
  private final Key key;
  private final String issuer;
  private final long accessExpMin;

  public JwtService(
      @Value("${app.security.jwt.secret}") String secret,
      @Value("${app.security.jwt.issuer}") String issuer,
      @Value("${app.security.jwt.access-exp-min}") long accessExpMin) {
    this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    this.issuer = issuer;
    this.accessExpMin = accessExpMin;
  }

  public String generateAccessToken(Long userId, String username, String role) {
    Instant now = Instant.now();
    Instant exp = now.plusSeconds(accessExpMin * 60);
    return Jwts.builder()
        .setIssuer(issuer)
        .setSubject(String.valueOf(userId))
        .setClaims(Map.of("u", username, "r", role))
        .setIssuedAt(Date.from(now))
        .setExpiration(Date.from(exp))
        .signWith(key, SignatureAlgorithm.HS256)
        .compact();
  }

  public Jws<Claims> parse(String token) {
    return Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
  }
}
