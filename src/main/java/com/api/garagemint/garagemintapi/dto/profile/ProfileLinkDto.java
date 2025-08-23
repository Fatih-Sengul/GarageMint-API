package com.api.garagemint.garagemintapi.dto.profile;

import com.api.garagemint.garagemintapi.model.profile.ProfileLinkType;
import lombok.*;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileLinkDto {
  private Long id;
  private ProfileLinkType type;   // INSTAGRAM, X, ...
  private String label;
  private String url;
  private Integer idx;
  private Boolean isPublic;
}
