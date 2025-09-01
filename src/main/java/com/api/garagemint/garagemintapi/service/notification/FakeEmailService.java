package com.api.garagemint.garagemintapi.service.notification;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@Slf4j
public class FakeEmailService implements EmailService {
  @Override
  public void sendAuctionWonEmail(Long winnerUserId, Long auctionId, BigDecimal amount) {
    log.info("[MAIL] Winner userId={} auctionId={} amount={}", winnerUserId, auctionId, amount);
  }
  @Override
  public void sendAuctionClosedEmailToSeller(Long sellerUserId, Long auctionId, BigDecimal highestAmount, Long winnerUserId) {
    log.info("[MAIL] Seller userId={} auctionId={} highest={} winner={}", sellerUserId, auctionId, highestAmount, winnerUserId);
  }
}
