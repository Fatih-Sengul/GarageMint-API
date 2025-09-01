package com.api.garagemint.garagemintapi.dto.auction;

import com.api.garagemint.garagemintapi.model.auction.AuctionStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuctionResponseDto {
  private Long id;
  private Long sellerUserId;
  private Long listingId;
  private BigDecimal startPrice;
  private String currency;
  private Instant startsAt;
  private Instant endsAt;
  private AuctionStatus status;
  private BigDecimal highestBidAmount;
  private Long highestBidUserId;
  private Instant createdAt;
  private Instant updatedAt;
}
