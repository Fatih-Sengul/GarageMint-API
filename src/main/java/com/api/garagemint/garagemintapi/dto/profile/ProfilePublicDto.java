package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;
import java.time.Instant;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfilePublicDto {
  private Long id;
  private Long userId;
  private String username;
  private String displayName;
  private String bio;
  private String avatarUrl;
  private String bannerUrl;
  private String location;
  private String websiteUrl;
  private String language;
  private Boolean isVerified;
  private Boolean isPublic;
  private Instant createdAt;
  private Instant updatedAt;

  private List<ProfileLinkDto> links;                 // public görünenler
  private List<ProfileFeaturedItemDto> featuredItems; // vitrin
  private ProfileStatsDto stats;                      // sayaçlar
}
