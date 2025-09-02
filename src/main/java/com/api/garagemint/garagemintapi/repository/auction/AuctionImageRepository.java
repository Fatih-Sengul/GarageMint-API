package com.api.garagemint.garagemintapi.repository.auction;

import com.api.garagemint.garagemintapi.model.auction.AuctionImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AuctionImageRepository extends JpaRepository<AuctionImage, Long> {
  List<AuctionImage> findByAuctionIdOrderByIdxAsc(Long auctionId);
  void deleteByAuctionId(Long auctionId);
  Optional<AuctionImage> findFirstByAuctionIdOrderByIdxAsc(Long auctionId);
}
