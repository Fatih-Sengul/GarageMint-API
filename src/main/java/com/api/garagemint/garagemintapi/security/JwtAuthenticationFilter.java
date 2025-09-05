package com.api.garagemint.garagemintapi.security;

import io.jsonwebtoken.Claims;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;

import java.io.IOException;

public class JwtAuthenticationFilter extends GenericFilter {

  private final JwtService jwt;

  public JwtAuthenticationFilter(JwtService jwt) { this.jwt = jwt; }

  @Override
  public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
    HttpServletRequest http = (HttpServletRequest) req;
    String auth = http.getHeader("Authorization");
    if (StringUtils.hasText(auth) && auth.startsWith("Bearer ")) {
      String token = auth.substring(7);
      try {
        var jws = jwt.parse(token);
        Claims c = jws.getBody();
        Long userId = Long.valueOf(c.getSubject());
        String username = (String) c.get("u");
        String role = "ROLE_" + (String) c.get("r");

        var principal = new AuthenticatedUser(userId, username, role);
        var authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
      } catch (Exception ignored) {}
    }
    chain.doFilter(req, res);
  }

  public static class AuthenticatedUser extends org.springframework.security.core.userdetails.User {
    private final Long userId;
    public AuthenticatedUser(Long userId, String username, String role) {
      super(username, "", java.util.List.of(new org.springframework.security.core.authority.SimpleGrantedAuthority(role)));
      this.userId = userId;
    }
    public Long getUserId() { return userId; }
  }
}
