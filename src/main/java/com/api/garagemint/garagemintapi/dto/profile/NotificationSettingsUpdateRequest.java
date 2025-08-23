package com.api.garagemint.garagemintapi.dto.profile;

import com.api.garagemint.garagemintapi.model.profile.DigestFrequency;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class NotificationSettingsUpdateRequest {
  private Boolean emailGeneral;
  private Boolean emailMessage;
  private Boolean emailFavorite;
  private Boolean emailListingActivity;
  private Boolean pushGeneral;
  private DigestFrequency digestFrequency;
}
