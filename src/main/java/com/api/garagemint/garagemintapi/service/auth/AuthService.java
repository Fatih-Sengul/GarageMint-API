package com.api.garagemint.garagemintapi.service.auth;

import com.api.garagemint.garagemintapi.dto.auth.*;
import com.api.garagemint.garagemintapi.model.auth.*;
import com.api.garagemint.garagemintapi.repository.auth.UserAccountRepository;
import com.api.garagemint.garagemintapi.security.JwtService;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthService {

  private final UserAccountRepository userRepo;
  private final PasswordEncoder pe;
  private final JwtService jwt;
  private final ProfileService profileService;

  @Transactional
  public JwtTokensDto register(RegisterRequest req) {
    if (userRepo.existsByEmailIgnoreCase(req.getEmail()))
      throw new BusinessRuleException("email already in use");
    if (userRepo.existsByUsernameIgnoreCase(req.getUsername()))
      throw new BusinessRuleException("username already in use");

    var user = UserAccount.builder()
        .email(req.getEmail().trim().toLowerCase())
        .username(req.getUsername().trim().toLowerCase())
        .password(pe.encode(req.getPassword()))
        .enabled(true)
        .role(UserRole.USER)
        .build();
    user = userRepo.save(user);

    profileService.ensureMyProfile(user.getId());

    String token = jwt.generateAccessToken(user.getId(), user.getUsername(), user.getRole().name());
    return JwtTokensDto.builder().accessToken(token).tokenType("Bearer").build();
  }

  @Transactional(readOnly = true)
  public JwtTokensDto login(LoginRequest req) {
    var u = userRepo.findByEmailIgnoreCase(req.getEmailOrUsername())
        .or(() -> userRepo.findByUsernameIgnoreCase(req.getEmailOrUsername()))
        .orElseThrow(() -> new BusinessRuleException("invalid credentials"));
    if (!u.isEnabled()) throw new BusinessRuleException("user disabled");
    if (!pe.matches(req.getPassword(), u.getPassword())) throw new BusinessRuleException("invalid credentials");

    String token = jwt.generateAccessToken(u.getId(), u.getUsername(), u.getRole().name());
    return JwtTokensDto.builder().accessToken(token).tokenType("Bearer").build();
  }

  public MeDto me(Long userId) {
    var u = userRepo.findById(userId).orElseThrow(() -> new BusinessRuleException("user not found"));
    return MeDto.builder().userId(u.getId()).username(u.getUsername()).email(u.getEmail()).build();
  }
}
