package com.api.garagemint.garagemintapi.model.cars;

import com.api.garagemint.garagemintapi.model.profile.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "series",
       uniqueConstraints = {
         @UniqueConstraint(name="ux_series_brand_slug", columnNames = {"brand_id","slug"})
       },
       indexes = {
         @Index(name="idx_series_brand", columnList = "brand_id"),
         @Index(name="idx_series_slug", columnList = "slug")
       })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Series extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** brands.id (FK – ilişki kurulmuyor, sadece sütun) */
  @Column(name="brand_id")
  private Long brandId;

  @Column(nullable=false, length=80)
  private String name;

  @Column(nullable=false, length=80)
  private String slug;
}

