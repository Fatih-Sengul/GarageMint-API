package com.api.garagemint.garagemintapi.model.auction;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "auction_bids",
  indexes = {
    @Index(name="idx_bids_auction", columnList = "auction_id"),
    @Index(name="idx_bids_bidder", columnList = "bidder_user_id")
  }
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AuctionBid extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name="auction_id", nullable=false, foreignKey = @ForeignKey(name="fk_bid_auction"))
  private Auction auction;

  @Column(name="bidder_user_id", nullable=false)
  private Long bidderUserId;

  @Column(name="amount", nullable=false, precision=18, scale=2)
  private BigDecimal amount;
}
