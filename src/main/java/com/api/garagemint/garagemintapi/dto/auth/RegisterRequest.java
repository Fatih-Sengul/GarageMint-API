package com.api.garagemint.garagemintapi.dto.auth;

import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegisterRequest {
  @Email
  @NotBlank
  private String email;
  @NotBlank
  @Pattern(regexp="^[a-z0-9_]{3,32}$")
  private String username;
  @NotBlank
  @Size(min=8, max=72)
  private String password;
}
