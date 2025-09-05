package com.api.garagemint.garagemintapi.controller.auction;

import com.api.garagemint.garagemintapi.dto.auction.*;
import com.api.garagemint.garagemintapi.security.SecurityUtil;
import com.api.garagemint.garagemintapi.service.auction.AuctionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/auctions", produces="application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"}, allowCredentials = "true")
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

  // ---- Seller ----
  @PostMapping
  public AuctionResponseDto create(@Valid @RequestBody AuctionCreateRequest req) {
    Long uid = SecurityUtil.getCurrentUserId();
    return auctionService.createAuction(uid, req);
  }

  @PutMapping("/{id}")
  public AuctionResponseDto update(@PathVariable Long id, @Valid @RequestBody AuctionUpdateRequest req) {
    Long uid = SecurityUtil.getCurrentUserId();
    return auctionService.updateAuction(uid, id, req);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    Long uid = SecurityUtil.getCurrentUserId();
    auctionService.deleteAuction(uid, id);
  }

  @PostMapping("/{id}/cancel")
  public AuctionResponseDto cancel(@PathVariable Long id) {
    Long uid = SecurityUtil.getCurrentUserId();
    return auctionService.cancelAuction(uid, id);
  }

  @PostMapping(value="/{id}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public List<AuctionImageDto> uploadImages(@PathVariable Long id, @RequestPart("files") List<MultipartFile> files) {
    Long uid = SecurityUtil.getCurrentUserId();
    return auctionService.uploadImages(uid, id, files);
  }

  // ---- Bidding ----
  @PostMapping("/{id}/bids")
  public BidResponseDto bid(@PathVariable Long id, @Valid @RequestBody BidCreateRequest req) {
    Long uid = SecurityUtil.getCurrentUserId();
    return auctionService.placeBid(uid, id, req);
  }
}
