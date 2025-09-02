package com.api.garagemint.garagemintapi.dto.auction;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AuctionImageDto {
  private Long id;

  @NotBlank @Size(max=500)
  private String url;

  @NotNull
  private Integer idx;
}
