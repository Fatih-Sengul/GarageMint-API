package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

import com.api.garagemint.garagemintapi.model.cars.Condition;
import com.api.garagemint.garagemintapi.model.cars.ListingType;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingCreateRequest {

  // Kimlik: sellerUserId backend'de SecurityContext'ten alınacak; request'te beklenmez.

  // ---- Meta ----
  @NotBlank @Size(max=180)
  private String title;

  @Size(max=20_000)
  private String description;

  // ---- Katalog alanları ----
  private Long brandId;           // optional
  private Long seriesId;          // optional

  @Size(max=180)
  private String modelName;

  @Size(max=16)
  private String scale;           // "1:64" gibi

  private Short modelYear;

  private Condition condition;       // NEW/MINT/USED/CUSTOM

  private Boolean limitedEdition; // default false

  @Size(max=80)
  private String theme;           // "JDM","F1","Le Mans","Movie Car"...

  @Size(max=64)
  private String countryOfOrigin;

  // ---- Fiyat ----
  @NotNull
  private ListingType type;            // SALE veya TRADE

  @Positive
  private BigDecimal price;       // TRADE ise null olabilir

  @Pattern(regexp="^[A-Z]{3}$")
  private String currency;        // ISO-4217, örn "TRY","USD"

  @Size(max=120)
  private String location;

  // ---- Çoklu seçmeli ----
  private List<Long> tagIds;                  // listing_tags
  private List<ListingImageUpsertDto> images; // listing_images
}
