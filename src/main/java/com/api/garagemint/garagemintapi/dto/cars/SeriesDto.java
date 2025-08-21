package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;
import jakarta.validation.constraints.*;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class SeriesDto {
  private Long id;

  private Long brandId;

  @NotBlank @Size(max=80)
  private String name;

  @NotBlank @Size(max=80)
  private String slug;
}
