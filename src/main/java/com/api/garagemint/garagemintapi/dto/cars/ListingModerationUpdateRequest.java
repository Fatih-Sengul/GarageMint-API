package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingModerationUpdateRequest {
  private Boolean isActive;   // admin override: feed’de görünmesin/görünsün
  private String status;      // admin düzeltmesi gerekiyorsa
}
