package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FollowUserDto {
  private Long id;            // profileId
  private String username;
  private String displayName;
  private String avatarUrl;
  private Boolean isVerified;
}

