package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingFilterRequest {

  // Kim arıyor? (opsiyonel)
  private Long sellerUserId;

  // Facet filtreleri
  private List<Long> brandIds;
  private List<Long> seriesIds;
  private List<Long> tagIds;

  private String theme;            // tekil string filtresi
  private String scale;            // "1:64"
  private String condition;        // "NEW","MINT","USED","CUSTOM"
  private Boolean limitedEdition;

  private String type;             // "SALE" / "TRADE"
  private String status;           // "ACTIVE","INACTIVE","SOLD"

  private String location;         // free-text

  private Short modelYearFrom;
  private Short modelYearTo;

  private BigDecimal priceMin;
  private BigDecimal priceMax;

  // Sıralama & sayfalama
  @Builder.Default
  private Integer page = 0;

  @Builder.Default
  private Integer size = 20;

  // createdAt, price, modelYear
  @Builder.Default
  private String sortBy = "createdAt";

  // ASC/DESC
  @Builder.Default
  private String sortDir = "DESC";
}
