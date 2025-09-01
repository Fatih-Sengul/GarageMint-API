package com.api.garagemint.garagemintapi.model.auction;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "auctions",
  indexes = {
    @Index(name="idx_auctions_status", columnList = "status"),
    @Index(name="idx_auctions_seller", columnList = "seller_user_id"),
    @Index(name="idx_auctions_starts_at", columnList = "starts_at"),
    @Index(name="idx_auctions_ends_at", columnList = "ends_at")
  }
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Auction extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  /** Mezatı açan kullanıcının user id'si */
  @Column(name="seller_user_id", nullable=false)
  private Long sellerUserId;

  /** Opsiyonel: İlan referansı (ayrı domain, fiyat/durum ayrıdır) */
  @Column(name="listing_id")
  private Long listingId;

  /** Başlangıç fiyatı (TRY) */
  @Column(name="start_price", nullable=false, precision=18, scale=2)
  private BigDecimal startPrice;

  /** TRY harici destek şimdilik yok; ileride çoklu currency için genişletilecek */
  @Column(name="currency", nullable=false, length=3)
  @Builder.Default
  private String currency = "TRY";

  /** Zaman penceresi */
  @Column(name="starts_at", nullable=false)
  private Instant startsAt;

  @Column(name="ends_at", nullable=false)
  private Instant endsAt;

  /** Durum makinesi */
  @Enumerated(EnumType.STRING)
  @Column(name="status", nullable=false, length=16)
  @Builder.Default
  private AuctionStatus status = AuctionStatus.SCHEDULED;

  /** Hızlı listeleme için denormalize alanlar */
  @Column(name="highest_bid_amount", precision=18, scale=2)
  private BigDecimal highestBidAmount;

  @Column(name="highest_bid_user_id")
  private Long highestBidUserId;

  /** Optimistic locking — eşzamanlı teklif yarışında güvenlik */
  @Version
  private Long version;
}
