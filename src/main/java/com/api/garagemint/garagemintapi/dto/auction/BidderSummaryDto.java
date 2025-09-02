package com.api.garagemint.garagemintapi.dto.auction;

import lombok.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class BidderSummaryDto {
  private Long userId;
  private String username;
  private String displayName;
  private String avatarUrl;
}
