package com.api.garagemint.garagemintapi.model.cars;

import com.api.garagemint.garagemintapi.model.profile.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "brands",
       indexes = {
         @Index(name="idx_brands_name", columnList = "name"),
         @Index(name="idx_brands_slug", columnList = "slug", unique = true)
       })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Brand extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, length=80)
  private String name;

  @Column(nullable=false, length=80, unique=true)
  private String slug;

  @Column(length=80)
  private String country;
}

