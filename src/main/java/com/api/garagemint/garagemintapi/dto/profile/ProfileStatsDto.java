package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;
import java.time.Instant;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileStatsDto {
  private Integer listingsActiveCount;
  private Integer listingsTotalCount;
  private Integer followersCount;
  private Integer followingCount;
  private Short   responseRate;
  private Instant lastActiveAt;
}
