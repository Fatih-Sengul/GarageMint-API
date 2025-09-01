package com.api.garagemint.garagemintapi.dto.auction;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.time.Instant;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AuctionCreateRequest {
  private Long listingId; // opsiyonel

  @NotNull @DecimalMin("0.01")
  private BigDecimal startPrice;

  /** null ise "now" ile başlat; yoksa ileri tarih olmalı */
  private Instant startsAt;

  /** zorunlu; (startsAt or now) + [1..15] gün aralığında olmalı */
  @NotNull
  private Instant endsAt;
}
