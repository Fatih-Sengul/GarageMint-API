package com.api.garagemint.garagemintapi.service.auction;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AuctionScheduler {

  private final AuctionService auctionService;

  /** Her dakikada bir, süresi dolan mezatları kapat */
  @Scheduled(fixedDelay = 60_000)
  public void closeExpired() {
    auctionService.closeExpiredAuctions();
  }
}
