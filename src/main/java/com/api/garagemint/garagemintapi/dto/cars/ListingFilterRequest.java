package com.api.garagemint.garagemintapi.dto.cars;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;
import java.util.List;

import com.api.garagemint.garagemintapi.model.cars.Condition;
import com.api.garagemint.garagemintapi.model.cars.ListingStatus;
import com.api.garagemint.garagemintapi.model.cars.ListingType;

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
  private Condition condition;        // NEW/MINT/USED/CUSTOM
  private Boolean limitedEdition;

  private ListingType type;             // SALE / TRADE
  private ListingStatus status;           // ACTIVE/INACTIVE/SOLD

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
