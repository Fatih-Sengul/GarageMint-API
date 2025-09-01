package com.api.garagemint.garagemintapi.repository.auction;

import com.api.garagemint.garagemintapi.model.auction.AuctionBid;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AuctionBidRepository extends JpaRepository<AuctionBid, Long> {
  List<AuctionBid> findByAuction_IdOrderByCreatedAtAsc(Long auctionId);
}
