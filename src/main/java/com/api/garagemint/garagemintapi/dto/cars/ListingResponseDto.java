package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

import com.api.garagemint.garagemintapi.model.cars.Condition;
import com.api.garagemint.garagemintapi.model.cars.ListingStatus;
import com.api.garagemint.garagemintapi.model.cars.ListingType;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingResponseDto {

  private Long id;

  // Seller (minimal kimlik)
  private ListingSellerDto seller;

  // Meta
  private String title;
  private String description;

  // Katalog
  private Long brandId;
  private Long seriesId;
  private String brandName;    // opsiyonel: response enrich
  private String seriesName;   // opsiyonel
  private String modelName;
  private String scale;
  private Short modelYear;
  private Condition condition;
  private Boolean limitedEdition;
  private String theme;
  private String countryOfOrigin;

  // Fiyat
  private ListingType type;         // SALE/TRADE
  private BigDecimal price;    // null → TRADE
  private String currency;
  private String location;

  // Moderasyon & state
  private ListingStatus status;       // ACTIVE/INACTIVE/SOLD
  private Boolean isActive;    // admin override

  // Zamanlar (ISO-8601)
  private String createdAt;
  private String updatedAt;

  // Medya & Etiketler
  private List<ListingImageDto> images;
  private List<TagDto> tags;
}
