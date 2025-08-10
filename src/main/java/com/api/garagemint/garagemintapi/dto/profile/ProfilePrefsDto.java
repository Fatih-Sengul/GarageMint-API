package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfilePrefsDto {
  private Boolean showEmail;
  private Boolean showLocation;
  private Boolean showLinks;
  private Boolean searchable;
  private Boolean allowDm;
  private Boolean showCollection;
  private Boolean showListings;
}
