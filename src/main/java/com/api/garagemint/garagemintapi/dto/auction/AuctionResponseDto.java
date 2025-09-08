package com.api.garagemint.garagemintapi.dto.auction;

import com.api.garagemint.garagemintapi.model.auction.AuctionStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuctionResponseDto {
  private Long id;
  private Long sellerUserId;
  private String sellerUsername;
  private Long listingId;
  private String title;
  private String description;
  private String brand;
  private String model;
  private String location;
  private BigDecimal startPrice;
  private String currency;
  private Instant startsAt;
  private Instant endsAt;
  private AuctionStatus status;
  private BigDecimal highestBidAmount;
  private Long highestBidUserId;
  private List<AuctionImageDto> images;
  private Instant createdAt;
  private Instant updatedAt;
}
