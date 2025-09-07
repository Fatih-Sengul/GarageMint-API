package com.api.garagemint.garagemintapi.service.auction;

import com.api.garagemint.garagemintapi.dto.auction.*;
import com.api.garagemint.garagemintapi.mapper.auction.AuctionMapper;
import com.api.garagemint.garagemintapi.model.auction.*;
import com.api.garagemint.garagemintapi.repository.auction.AuctionBidRepository;
import com.api.garagemint.garagemintapi.repository.auction.AuctionRepository;
import com.api.garagemint.garagemintapi.repository.auction.AuctionImageRepository;
import com.api.garagemint.garagemintapi.repository.profiles.ProfileRepository;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import com.api.garagemint.garagemintapi.service.notification.EmailService;
import com.api.garagemint.garagemintapi.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuctionService {

  private static final BigDecimal MIN_INCREMENT = new BigDecimal("10.00"); // 10 TL

  private final AuctionRepository auctionRepo;
  private final AuctionBidRepository bidRepo;
  private final AuctionImageRepository imageRepo;
  private final ProfileRepository profileRepo;
  private final AuctionMapper mapper;
  private final EmailService emailService;
  private final FileStorageService storage;

  /* ========= PUBLIC ========= */

  @Transactional(readOnly = true)
  public AuctionResponseDto getAuction(Long id) {
    var a = auctionRepo.findById(id).orElseThrow(() -> new NotFoundException("Auction not found"));
    var dto = mapper.toDto(a);
    var images = imageRepo.findByAuctionIdOrderByIdxAsc(a.getId());
    dto.setImages(mapper.toImageDtoList(images));
    return dto;
  }

  @Transactional(readOnly = true)
  public List<AuctionListItemDto> listActiveAuctions() {
    return auctionRepo.findByStatus(AuctionStatus.ACTIVE).stream()
        .map(a -> {
            var cover = imageRepo.findFirstByAuctionIdOrderByIdxAsc(a.getId())
                .map(AuctionImage::getUrl).orElse(null);
            return AuctionListItemDto.builder()
                .id(a.getId())
                .listingId(a.getListingId())
                .startPrice(a.getStartPrice())
                .highestBidAmount(a.getHighestBidAmount())
                .currency(a.getCurrency())
                .status(a.getStatus())
                .endsAt(a.getEndsAt())
                .coverUrl(cover)
                .build();
        })
        .toList();
  }

  @Transactional(readOnly = true)
  public List<BidResponseDto> getBids(Long auctionId) {
    var a = auctionRepo.findById(auctionId).orElseThrow(() -> new NotFoundException("Auction not found"));
    return bidRepo.findByAuction_IdOrderByCreatedAtAsc(a.getId()).stream()
        .map(b -> {
            var dto = mapper.toDto(b);
            profileRepo.findByUserId(dto.getBidderUserId()).ifPresent(p ->
                dto.setBidder(BidderSummaryDto.builder()
                    .userId(p.getUserId())
                    .username(p.getUsername())
                    .displayName(p.getDisplayName())
                    .avatarUrl(p.getAvatarUrl())
                    .build())
            );
            return dto;
        })
        .toList();
  }

  /* ========= SELLER ========= */

  @Transactional(readOnly = true)
  public List<AuctionListItemDto> listAuctionsBySeller(Long sellerUserId) {
    if (sellerUserId == null) throw new ValidationException("sellerUserId is required");
    return auctionRepo.findBySellerUserId(sellerUserId).stream()
        .map(a -> {
            var cover = imageRepo.findFirstByAuctionIdOrderByIdxAsc(a.getId())
                .map(AuctionImage::getUrl).orElse(null);
            return AuctionListItemDto.builder()
                .id(a.getId())
                .listingId(a.getListingId())
                .startPrice(a.getStartPrice())
                .highestBidAmount(a.getHighestBidAmount())
                .currency(a.getCurrency())
                .status(a.getStatus())
                .endsAt(a.getEndsAt())
                .coverUrl(cover)
                .build();
        })
        .toList();
  }

  @Transactional
  public AuctionResponseDto createAuction(Long sellerUserId, AuctionCreateRequest req) {
    if (sellerUserId == null) throw new ValidationException("sellerUserId is required");
    if (req == null) throw new ValidationException("request body is required");

    var now = Instant.now();
    var startsAt = (req.getStartsAt() == null) ? now : req.getStartsAt();
    if (startsAt.isBefore(now.minusSeconds(5))) {
      throw new ValidationException("startsAt cannot be in the past");
    }
    if (req.getEndsAt().isBefore(startsAt)) {
      throw new ValidationException("endsAt must be after startsAt");
    }
    var dur = Duration.between(startsAt, req.getEndsAt()).toDays();
    if (dur < 1 || dur > 15) {
      throw new BusinessRuleException("Auction duration must be between 1 and 15 days");
    }

    var a = Auction.builder()
        .sellerUserId(sellerUserId)
        .listingId(req.getListingId())
        .title(req.getTitle())
        .description(req.getDescription())
        .brand(req.getBrand())
        .model(req.getModel())
        .location(req.getLocation())
        .startPrice(req.getStartPrice())
        .currency("TRY")
        .startsAt(startsAt)
        .endsAt(req.getEndsAt())
        .status(startsAt.isAfter(now) ? AuctionStatus.SCHEDULED : AuctionStatus.ACTIVE)
        .highestBidAmount(null)
        .highestBidUserId(null)
        .build();

    a = auctionRepo.save(a);
    return mapper.toDto(a);
  }

  @Transactional
  public AuctionResponseDto cancelAuction(Long sellerUserId, Long auctionId) {
    var a = auctionRepo.findById(auctionId).orElseThrow(() -> new NotFoundException("Auction not found"));
    if (!a.getSellerUserId().equals(sellerUserId)) throw new BusinessRuleException("Forbidden");
    if (a.getStatus() == AuctionStatus.ENDED) throw new BusinessRuleException("Auction already ended");
    // Tercihen: ilk bid gelmeden iptal; basitleştirelim:
    if (a.getStatus() == AuctionStatus.ACTIVE && a.getHighestBidAmount() != null) {
      throw new BusinessRuleException("Cannot cancel after first bid");
    }
    a.setStatus(AuctionStatus.CANCELLED);
    auctionRepo.save(a);
    return mapper.toDto(a);
  }

  @Transactional
  public AuctionResponseDto updateAuction(Long sellerUserId, Long auctionId, AuctionUpdateRequest req) {
    var a = auctionRepo.findById(auctionId).orElseThrow(() -> new NotFoundException("Auction not found"));
    if (!a.getSellerUserId().equals(sellerUserId)) throw new BusinessRuleException("Not owner");
    if (a.getStatus() == AuctionStatus.ENDED) throw new BusinessRuleException("Auction already ended");

    Instant oldEndsAt = a.getEndsAt();
    mapper.updateEntity(a, req);

    if (req.getEndsAt() != null) {
      if (a.getEndsAt().isBefore(a.getStartsAt())) {
        throw new ValidationException("endsAt cannot be before startsAt");
      }
      if (a.getStatus() == AuctionStatus.ACTIVE && a.getEndsAt().isBefore(oldEndsAt)) {
        throw new BusinessRuleException("cannot shorten active auction");
      }
    }

    auctionRepo.save(a);
    return mapper.toDto(a);
  }

  @Transactional
  public void deleteAuction(Long sellerUserId, Long auctionId) {
    var a = auctionRepo.findById(auctionId).orElseThrow(() -> new NotFoundException("Auction not found"));
    if (!a.getSellerUserId().equals(sellerUserId)) throw new BusinessRuleException("Not owner");
    a.setStatus(AuctionStatus.CANCELLED);
    auctionRepo.save(a);
  }

  /* ========= BIDDING ========= */

  @Transactional
  public BidResponseDto placeBid(Long bidderUserId, Long auctionId, BidCreateRequest req) {
    if (bidderUserId == null) throw new ValidationException("bidderUserId is required");
    if (req == null) throw new ValidationException("request body is required");

    // Lock with optimistic versioning (force increment) to avoid race
    var a = auctionRepo.findWithOptimisticLockingById(auctionId);
    var now = Instant.now();

    if (a.getStatus() == AuctionStatus.SCHEDULED && now.isAfter(a.getStartsAt())) {
      a.setStatus(AuctionStatus.ACTIVE);
    }
    if (a.getStatus() != AuctionStatus.ACTIVE) throw new BusinessRuleException("Auction not active");
    if (now.isAfter(a.getEndsAt())) throw new BusinessRuleException("Auction already ended");
    if (a.getSellerUserId().equals(bidderUserId)) throw new BusinessRuleException("Seller cannot bid");

    var minRequired = (a.getHighestBidAmount() == null)
        ? a.getStartPrice()
        : a.getHighestBidAmount().add(MIN_INCREMENT);

    if (req.getAmount().compareTo(minRequired) < 0) {
      throw new BusinessRuleException("Bid must be >= " + minRequired);
    }

    var bid = AuctionBid.builder()
        .auction(a)
        .bidderUserId(bidderUserId)
        .amount(req.getAmount())
        .build();
    bid = bidRepo.save(bid);

    a.setHighestBidAmount(req.getAmount());
    a.setHighestBidUserId(bidderUserId);
    auctionRepo.save(a);

    var dto = mapper.toDto(bid);
    profileRepo.findByUserId(bidderUserId).ifPresent(p ->
        dto.setBidder(BidderSummaryDto.builder()
            .userId(p.getUserId())
            .username(p.getUsername())
            .displayName(p.getDisplayName())
            .avatarUrl(p.getAvatarUrl())
            .build())
    );
    return dto;
  }

  @Transactional
  public List<AuctionImageDto> uploadImages(Long sellerUserId, Long auctionId, List<MultipartFile> files) {
    if (files == null || files.isEmpty()) throw new ValidationException("files are required");
    if (files.size() > 3) throw new ValidationException("max 3 images");

    var a = auctionRepo.findById(auctionId).orElseThrow(() -> new NotFoundException("Auction not found"));
    if (!a.getSellerUserId().equals(sellerUserId)) throw new BusinessRuleException("Forbidden");

    imageRepo.deleteByAuctionId(auctionId);

    List<AuctionImage> imgs = new java.util.ArrayList<>();
    int idx = 0;
    for (MultipartFile f : files) {
      String url = storage.saveImage(f, "auctions");
      imgs.add(AuctionImage.builder().auctionId(auctionId).url(url).idx(idx++).build());
    }

    var saved = imageRepo.saveAll(imgs);
    return mapper.toImageDtoList(saved);
  }

  /* ========= CLOSURE / SCHEDULER ========= */

  @Transactional
  public void closeExpiredAuctions() {
    var now = Instant.now();
    var list = auctionRepo.findByStatusAndEndsAtBefore(AuctionStatus.ACTIVE, now);
    for (var a : list) {
      a.setStatus(AuctionStatus.ENDED);
      auctionRepo.save(a);

      // Email notifications
      if (a.getHighestBidUserId() != null) {
        emailService.sendAuctionWonEmail(a.getHighestBidUserId(), a.getId(), a.getHighestBidAmount());
      }
      emailService.sendAuctionClosedEmailToSeller(a.getSellerUserId(), a.getId(), a.getHighestBidAmount(), a.getHighestBidUserId());
    }
  }

  /* ========= HELPERS ========= */
}
