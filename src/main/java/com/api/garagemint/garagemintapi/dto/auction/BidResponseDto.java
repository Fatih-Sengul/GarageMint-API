package com.api.garagemint.garagemintapi.dto.auction;

import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BidResponseDto {
  private Long id;
  private Long auctionId;
  private Long bidderUserId;
  private BigDecimal amount;
  private Instant createdAt;
}
