package com.api.garagemint.garagemintapi.security;

import com.api.garagemint.garagemintapi.model.auth.UserAccount;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public record UserPrincipal(Long id, String username, String email, String role) implements UserDetails {
  public static UserPrincipal from(UserAccount u) {
    return new UserPrincipal(u.getId(), u.getUsername(), u.getEmail(), "ROLE_" + u.getRole().name());
  }
  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority(role));
  }
  @Override
  public String getPassword() { return null; }
  @Override
  public String getUsername() { return username; }
  @Override
  public boolean isAccountNonExpired() { return true; }
  @Override
  public boolean isAccountNonLocked() { return true; }
  @Override
  public boolean isCredentialsNonExpired() { return true; }
  @Override
  public boolean isEnabled() { return true; }
}
