package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class NotificationSettingsDto {
  private Boolean emailGeneral;
  private Boolean emailMessage;
  private Boolean emailFavorite;
  private Boolean emailListingActivity;
  private Boolean pushGeneral;
  private String  digestFrequency; // OFF | DAILY | WEEKLY
}
