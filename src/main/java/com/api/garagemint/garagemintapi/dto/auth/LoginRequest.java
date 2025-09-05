package com.api.garagemint.garagemintapi.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LoginRequest {
  @NotBlank
  private String emailOrUsername;
  @NotBlank
  private String password;
}
