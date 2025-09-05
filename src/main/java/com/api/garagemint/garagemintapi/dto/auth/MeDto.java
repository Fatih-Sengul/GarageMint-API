package com.api.garagemint.garagemintapi.dto.auth;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MeDto {
  private Long userId;
  private String username;
  private String email;
}
