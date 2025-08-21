package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

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
  private String condition;
  private Boolean limitedEdition;
  private String theme;
  private String countryOfOrigin;

  // Fiyat
  private String type;         // SALE/TRADE
  private BigDecimal price;    // null → TRADE
  private String currency;
  private String location;

  // Moderasyon & state
  private String status;       // ACTIVE/SOLD/WITHDRAWN
  private Boolean isActive;    // admin override

  // Zamanlar (ISO-8601)
  private String createdAt;
  private String updatedAt;

  // Medya & Etiketler
  private List<ListingImageDto> images;
  private List<TagDto> tags;
}
