package com.api.garagemint.garagemintapi.controller.auction;

import com.api.garagemint.garagemintapi.dto.auction.*;
import com.api.garagemint.garagemintapi.service.auction.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/auctions", produces="application/json")
@RequiredArgsConstructor
public class AuctionController {

  private final AuctionService auctionService;

  // ---- Public ----
  @GetMapping("/{id}")
  public AuctionResponseDto get(@PathVariable Long id) {
    return auctionService.getAuction(id);
  }

  @GetMapping
  public List<AuctionListItemDto> listActive() {
    return auctionService.listActiveAuctions();
  }

  @GetMapping("/{id}/bids")
  public List<BidResponseDto> bids(@PathVariable Long id) {
    return auctionService.getBids(id);
  }

  // ---- Seller (mock userId = 1L) ----
  @PostMapping
  public AuctionResponseDto create(@Valid @RequestBody AuctionCreateRequest req) {
    return auctionService.createAuction(1L, req);
  }

  @PostMapping("/{id}/cancel")
  public AuctionResponseDto cancel(@PathVariable Long id) {
    return auctionService.cancelAuction(1L, id);
  }

  // ---- Bidding (mock userId = 2L) ----
  // Not: Gerçekte bidder userId, SecurityContext'ten gelecek
  @PostMapping("/{id}/bids")
  public BidResponseDto bid(@PathVariable Long id, @Valid @RequestBody BidCreateRequest req) {
    return auctionService.placeBid(2L, id, req);
  }
}
