package com.api.garagemint.garagemintapi.dto.auction;

import com.api.garagemint.garagemintapi.model.auction.AuctionStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuctionListItemDto {
  private Long id;
  private Long sellerUserId;
  private String sellerUsername;
  private Long listingId;
  private BigDecimal startPrice;
  private BigDecimal highestBidAmount;
  private String currency;
  private AuctionStatus status;
  private Instant endsAt;
  private String coverUrl;
}
