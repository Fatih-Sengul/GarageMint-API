package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationSettingsUpdateRequest {
  private Boolean emailGeneral;
  private Boolean emailMessage;
  private Boolean emailFavorite;
  private Boolean emailListingActivity;
  private Boolean pushGeneral;
  private String  digestFrequency;
}
