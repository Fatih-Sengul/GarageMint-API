package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Pattern;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileUpdateRequest {
  @Pattern(regexp = "^[a-z0-9_]{3,32}$")
  private String username;

  @Size(max=80)
  private String displayName;

  @Size(max=500)
  private String bio;

  @Size(max=120)
  private String location;

  @Size(max=250)
  private String websiteUrl;

  @Size(max=8)
  private String language;

  private Boolean isPublic;
}
