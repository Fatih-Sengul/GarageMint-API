package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingSellerDto {
  private Long userId;        // seller_user_id
  private String username;    // opsiyonel - profile’dan doldurulacak
  private String displayName; // opsiyonel
  private String avatarUrl;   // opsiyonel
  private String location;    // opsiyonel
}
