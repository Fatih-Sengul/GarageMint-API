package com.api.garagemint.garagemintapi.model.auction;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "auction_images",
       uniqueConstraints = @UniqueConstraint(name="ux_auction_image_idx", columnNames = {"auction_id","idx"}),
       indexes = @Index(name="idx_auction_images_auction", columnList = "auction_id"))
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class AuctionImage extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(name="auction_id", nullable=false)
  private Long auctionId;

  @Column(nullable=false, length=500)
  private String url;

  @Column(nullable=false)
  private Integer idx;
}
