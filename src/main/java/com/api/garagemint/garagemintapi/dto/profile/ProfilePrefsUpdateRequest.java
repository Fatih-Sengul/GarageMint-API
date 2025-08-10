package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfilePrefsUpdateRequest {
  private Boolean showEmail;
  private Boolean showLocation;
  private Boolean showLinks;
  private Boolean searchable;
  private Boolean allowDm;
  private Boolean showCollection;
  private Boolean showListings;
}
