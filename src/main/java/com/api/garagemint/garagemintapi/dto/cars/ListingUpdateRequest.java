package com.api.garagemint.garagemintapi.dto.cars;

import com.api.garagemint.garagemintapi.model.cars.ListingStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ListingUpdateRequest {

  @Size(max = 180)
  private String title;

  @Size(max = 2_000)
  private String description;

  @Positive
  private BigDecimal price;

  @Pattern(regexp = "^[A-Z]{3}$")
  private String currency;

  private ListingStatus status;
}

