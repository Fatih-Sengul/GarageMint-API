package com.api.garagemint.garagemintapi.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

public final class SecurityUtil {
  private SecurityUtil() {}
  public static Long getCurrentUserId() {
    Authentication a = SecurityContextHolder.getContext().getAuthentication();
    if (a == null || !(a.getPrincipal() instanceof JwtAuthenticationFilter.AuthenticatedUser p)) return null;
    return p.getUserId();
  }
}
