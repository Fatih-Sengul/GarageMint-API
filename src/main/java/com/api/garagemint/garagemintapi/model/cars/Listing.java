package com.api.garagemint.garagemintapi.model.cars;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "listings",
       indexes = {
         @Index(name="idx_listings_status_created", columnList = "status,created_at"),
         @Index(name="idx_listings_seller", columnList = "seller_user_id"),
         @Index(name="idx_listings_brand", columnList = "brand_id"),
         @Index(name="idx_listings_series", columnList = "series_id"),
         @Index(name="idx_listings_theme", columnList = "theme"),
         @Index(name="idx_listings_limited", columnList = "limited_edition")
       })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Listing extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** users.id (FK – ilişki kurulmuyor, sadece sütun) */
  @Column(name="seller_user_id", nullable=false)
  private Long sellerUserId;

  /** brands.id (FK – opsiyonel) */
  @Column(name="brand_id")
  private Long brandId;

  /** series.id (FK – opsiyonel) */
  @Column(name="series_id")
  private Long seriesId;

  @Column(nullable=false, length=180)
  private String title;

  @Lob
  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(name="model_name", length=180)
  private String modelName;

  @Column(length=16)
  private String scale;

  @Column(name="model_year")
  private Short modelYear;

  @Enumerated(EnumType.STRING)
  @Column(length=24)
  private Condition condition;

  @Column(name="limited_edition")
  private Boolean limitedEdition = Boolean.FALSE;

  @Column(length=80)
  private String theme;

  @Column(name="country_of_origin", length=64)
  private String countryOfOrigin;

  // ---- Pricing ----
  @Enumerated(EnumType.STRING)
  @Column(length=16, nullable=false)
  private ListingType type;

  private BigDecimal price;

  @Column(length=3)
  private String currency;

  @Column(length=120)
  private String location;

  @Enumerated(EnumType.STRING)
  @Column(length=16)
  private ListingStatus status;

  /** Admin moderasyonu için; false → feed’de görünmesin */
  @Column(name="is_active")
  private Boolean isActive = Boolean.TRUE;
}

