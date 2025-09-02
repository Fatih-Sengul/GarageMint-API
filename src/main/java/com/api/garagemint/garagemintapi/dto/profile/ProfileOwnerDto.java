package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;
import java.time.Instant;
import java.util.List;
import com.api.garagemint.garagemintapi.dto.cars.ListingResponseDto;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileOwnerDto {
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

  private List<ProfileLinkDto> links;
  private ProfilePrefsDto prefs;
  private NotificationSettingsDto notificationSettings;
  private ProfileStatsDto stats;

  private Integer followersCount;
  private Integer followingCount;

  private List<ListingResponseDto> listings;
}
