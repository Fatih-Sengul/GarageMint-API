package com.api.garagemint.garagemintapi.dto.auction;

import com.api.garagemint.garagemintapi.model.auction.AuctionStatus;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuctionUpdateRequest {

  @Size(max = 180)
  private String title;

  @Size(max = 4000)
  private String description;

  @Size(max = 80)
  private String brand;

  @Size(max = 80)
  private String model;

  @Size(max = 120)
  private String location;

  private Instant endsAt;

  private AuctionStatus status;
}

