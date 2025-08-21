package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingImageUpsertDto {

  @NotBlank @Size(max=500)
  private String url;

  @NotNull
  private Integer idx;
}
