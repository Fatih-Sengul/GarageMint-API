package com.api.garagemint.garagemintapi.model.cars;

import com.api.garagemint.garagemintapi.model.profile.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "listing_images",
       uniqueConstraints = @UniqueConstraint(name="ux_listing_image_idx", columnNames = {"listing_id","idx"}),
       indexes = @Index(name="idx_listing_images_listing", columnList = "listing_id"))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingImage extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="listing_id", nullable=false)
  private Long listingId;

  @Column(nullable=false, length=500)
  private String url;

  @Column(nullable=false)
  private Integer idx;
}

