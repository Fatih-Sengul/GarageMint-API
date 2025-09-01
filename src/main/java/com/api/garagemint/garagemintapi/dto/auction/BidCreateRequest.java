package com.api.garagemint.garagemintapi.dto.auction;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class BidCreateRequest {
  @NotNull @DecimalMin("0.01")
  private BigDecimal amount;
}
