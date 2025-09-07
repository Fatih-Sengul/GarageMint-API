package com.api.garagemint.garagemintapi.controller.auction;

import com.api.garagemint.garagemintapi.dto.auction.*;
import com.api.garagemint.garagemintapi.security.AuthUser;
import com.api.garagemint.garagemintapi.service.auction.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

  @GetMapping("/seller")
  public List<AuctionListItemDto> listBySeller(
      @AuthenticationPrincipal AuthUser user,
      @RequestParam(value = "userId", required = false) Long userId) {
    Long uid = (userId != null) ? userId : (user != null ? user.id() : null);
    return auctionService.listAuctionsBySeller(uid);
  }

  // ---- Seller ----
  @PostMapping
  public AuctionResponseDto create(
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody AuctionCreateRequest req) {
    return auctionService.createAuction(user.id(), req);
  }

  @PutMapping("/{id}")
  public AuctionResponseDto update(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id,
      @Valid @RequestBody AuctionUpdateRequest req) {
    return auctionService.updateAuction(user.id(), id, req);
  }

  @DeleteMapping("/{id}")
  public void delete(@AuthenticationPrincipal AuthUser user, @PathVariable Long id) {
    auctionService.deleteAuction(user.id(), id);
  }

  @PostMapping("/{id}/cancel")
  public AuctionResponseDto cancel(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id) {
    return auctionService.cancelAuction(user.id(), id);
  }

  @PostMapping(value="/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public List<AuctionImageDto> uploadImages(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id,
      @RequestPart("files") List<MultipartFile> files) {
    return auctionService.uploadImages(user.id(), id, files);
  }

  // ---- Bidding ----
  @PostMapping("/{id}/bids")
  public BidResponseDto bid(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id,
      @Valid @RequestBody BidCreateRequest req) {
    return auctionService.placeBid(user.id(), id, req);
  }
}
