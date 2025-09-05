package com.api.garagemint.garagemintapi.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JwtTokensDto {
  private String accessToken;
  private String tokenType;
}
