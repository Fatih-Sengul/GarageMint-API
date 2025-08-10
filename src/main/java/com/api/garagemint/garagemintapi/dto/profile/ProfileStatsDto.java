package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;
import java.time.Instant;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileStatsDto {
  private Integer itemsCount;
  private Integer listingsActiveCount;
  private Integer favoritesCount;
  private Integer followersCount;
  private Short   responseRate;
  private Instant lastActiveAt;
}
