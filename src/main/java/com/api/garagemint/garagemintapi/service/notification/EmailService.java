package com.api.garagemint.garagemintapi.service.notification;

import java.math.BigDecimal;

public interface EmailService {
  void sendAuctionWonEmail(Long winnerUserId, Long auctionId, BigDecimal amount);
  void sendAuctionClosedEmailToSeller(Long sellerUserId, Long auctionId, BigDecimal highestAmount, Long winnerUserId);
}
