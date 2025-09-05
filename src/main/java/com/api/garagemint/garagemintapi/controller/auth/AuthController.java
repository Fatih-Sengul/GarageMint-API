package com.api.garagemint.garagemintapi.controller.auth;

import com.api.garagemint.garagemintapi.dto.auth.*;
import com.api.garagemint.garagemintapi.security.SecurityUtil;
import com.api.garagemint.garagemintapi.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api/v1/auth", produces="application/json")
@RequiredArgsConstructor
public class AuthController {

  private final AuthService auth;

  @PostMapping("/register")
  public JwtTokensDto register(@Valid @RequestBody RegisterRequest req) {
    return auth.register(req);
  }

  @PostMapping("/login")
  public JwtTokensDto login(@Valid @RequestBody LoginRequest req) {
    return auth.login(req);
  }

  @GetMapping("/me")
  public MeDto me() {
    Long uid = SecurityUtil.getCurrentUserId();
    if (uid == null) throw new RuntimeException("unauthorized");
    return auth.me(uid);
  }
}
