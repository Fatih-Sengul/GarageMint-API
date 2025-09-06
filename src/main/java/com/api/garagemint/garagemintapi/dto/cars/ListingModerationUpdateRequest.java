package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;

import com.api.garagemint.garagemintapi.model.cars.ListingStatus;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingModerationUpdateRequest {
  private Boolean isActive;   // admin override: feed’de görünmesin/görünsün
  private ListingStatus status;      // admin düzeltmesi gerekiyorsa
}
