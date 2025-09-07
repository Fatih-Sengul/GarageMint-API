package com.api.garagemint.garagemintapi.repository.auction;

import com.api.garagemint.garagemintapi.model.auction.Auction;
import com.api.garagemint.garagemintapi.model.auction.AuctionStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.QueryHints;

import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import java.time.Instant;
import java.util.List;

public interface AuctionRepository extends JpaRepository<Auction, Long> {

  List<Auction> findByStatusAndEndsAtBefore(AuctionStatus status, Instant now);

  List<Auction> findByStatus(AuctionStatus status);

  List<Auction> findBySellerUserId(Long userId);

  /** Teklif anında optimistic lock yeterli; yine de isteğe bağlı PESSIMISTIC_WRITE desteklemek için aşağıdaki method eklenebilir */
  @Lock(LockModeType.OPTIMISTIC_FORCE_INCREMENT)
  @QueryHints(@QueryHint(name = "jakarta.persistence.lock.timeout", value = "5000"))
  Auction findWithOptimisticLockingById(Long id);
}
