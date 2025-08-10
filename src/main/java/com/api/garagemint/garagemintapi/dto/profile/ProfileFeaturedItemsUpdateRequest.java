package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ProfileFeaturedItemsUpdateRequest {
  private List<ProfileFeaturedItemDto> items; // {itemId, idx}
}
