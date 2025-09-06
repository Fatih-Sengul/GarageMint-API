package com.api.garagemint.garagemintapi.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

public class JwtAuthenticationFilter extends GenericFilter {

  private final JwtService jwt;

  public JwtAuthenticationFilter(JwtService jwt) { this.jwt = jwt; }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest http = (HttpServletRequest) req;
    HttpServletResponse httpRes = (HttpServletResponse) res;
    String auth = http.getHeader("Authorization");
    if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      try {
        Claims c = jwt.parse(token).getBody();
        Number uidNum = c.get("uid", Number.class);
        if (uidNum == null) {
          httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
          return;
        }
        long userId = uidNum.longValue();
        String username = c.getSubject();
        String role = c.get("role", String.class);
        var principal = new AuthUser(userId, username);
        var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + (role != null ? role : "USER")));
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, authorities);
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception e) {
        httpRes.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        return;
      }
    }
    chain.doFilter(req, res);
  }
}
