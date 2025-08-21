package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingUpdateRequest {

  @Size(max=180)
  private String title;

  @Size(max=20_000)
  private String description;

  private Long brandId;
  private Long seriesId;

  @Size(max=180)
  private String modelName;

  @Size(max=16)
  private String scale;

  private Short year;

  @Size(max=24)
  private String condition;

  private Boolean limitedEdition;

  @Size(max=80)
  private String theme;

  @Size(max=64)
  private String countryOfOrigin;

  // ---- Fiyat ----
  @Size(max=16)
  private String type;            // "SALE"/"TRADE"

  @Positive
  private BigDecimal price;

  @Pattern(regexp="^[A-Z]{3}$")
  private String currency;

  @Size(max=120)
  private String location;

  // ---- Çoklu seçmeli ----
  private List<Long> tagIds;
  private List<ListingImageUpsertDto> images;

  // ---- Kullanıcı güncellemeleri için opsiyonel (status genelde PATCH ile ayrı yönetilir) ----
  @Size(max=16)
  private String status;          // "ACTIVE","SOLD","WITHDRAWN"
}
