package com.api.garagemint.garagemintapi.service.cars;

import com.api.garagemint.garagemintapi.dto.cars.*;
import com.api.garagemint.garagemintapi.mapper.cars.ListingMapper;
import com.api.garagemint.garagemintapi.model.cars.*;
import com.api.garagemint.garagemintapi.model.profile.Profile;
import com.api.garagemint.garagemintapi.repository.ProfileRepository;
import com.api.garagemint.garagemintapi.repository.cars.*;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service @RequiredArgsConstructor
public class ListingService {

  private static final int MAX_ACTIVE_LISTINGS_FREE = 3;

  private final ListingRepository listingRepo;
  private final ListingQueryRepository listingQueryRepo;
  private final ListingImageRepository imageRepo;
  private final ListingTagRepository listingTagRepo;
  private final TagRepository tagRepo;
  private final BrandRepository brandRepo;
  private final SeriesRepository seriesRepo;
  private final ProfileRepository profileRepo;
  private final ListingMapper mapper;

  /* =============== CREATE =============== */

  @Transactional
  public ListingResponseDto create(Long sellerUserId, ListingCreateRequest req) {
    validateCreate(req);

    // active quota
    long activeCount = listingRepo.countBySellerUserIdAndStatus(sellerUserId, ListingStatus.ACTIVE);
    if (activeCount >= MAX_ACTIVE_LISTINGS_FREE)
      throw new BusinessRuleException("maximum active listings reached: " + MAX_ACTIVE_LISTINGS_FREE);

    var entity = mapper.toEntity(req);
    entity.setSellerUserId(sellerUserId);
    entity.setStatus(ListingStatus.ACTIVE);
    entity.setIsActive(Boolean.TRUE);

    // SALE → price/currency zorunlu
    if (entity.getType() == ListingType.SALE) {
      if (entity.getPrice() == null || entity.getPrice().compareTo(BigDecimal.ZERO) <= 0)
        throw new ValidationException("price must be > 0 for SALE");
      if (req.getCurrency() == null || req.getCurrency().isBlank())
        throw new ValidationException("currency is required for SALE");
      entity.setCurrency(req.getCurrency());
    } else {
      entity.setPrice(null);
      entity.setCurrency(null);
    }

    // existence checks
    if (entity.getBrandId() != null) ensureBrandExists(entity.getBrandId());
    if (entity.getSeriesId() != null) ensureSeriesExists(entity.getSeriesId());

    var saved = listingRepo.save(entity);

    // replace semantics
    upsertImagesInternal(saved.getId(), req.getImages());
    upsertTagsInternal(saved.getId(), req.getTagIds());

    return assembleResponse(saved.getId());
  }

  /* =============== READ =============== */

  @Transactional(readOnly = true)
  public ListingResponseDto getPublicById(Long id) {
    var e = listingRepo.findById(id).orElseThrow(() -> new NotFoundException("Listing not found"));
    if (!Boolean.TRUE.equals(e.getIsActive()) || e.getStatus() != ListingStatus.ACTIVE)
      throw new NotFoundException("Listing not available");
    return assembleResponse(id);
  }

  @Transactional(readOnly = true)
  public ListingResponseDto getMyById(Long sellerUserId, Long id) {
    var e = load(id);
    ensureOwner(sellerUserId, e);
    return assembleResponse(id);
  }

  @Transactional(readOnly = true)
  public Page<ListingResponseDto> search(ListingFilterRequest filter) {
    Pageable pageable = PageRequest.of(
        Optional.ofNullable(filter.getPage()).orElse(0),
        Optional.ofNullable(filter.getSize()).orElse(20)
    );
    var page = listingQueryRepo.search(filter, pageable);

    // NOTE: assembleResponse her id için ayrı repository çağrıları yapar; büyürse DTO projection optimize edilir.
    var dtos = page.getContent().stream().map(l -> assembleResponse(l.getId())).toList();
    return new PageImpl<>(dtos, pageable, page.getTotalElements());
  }

  @Transactional(readOnly = true)
  public List<ListingResponseDto> listMyActive(Long sellerUserId) {
    return listingRepo.findBySellerUserIdAndStatus(sellerUserId, ListingStatus.ACTIVE)
        .stream().map(l -> assembleResponse(l.getId())).toList();
  }

  @Transactional(readOnly = true)
  public List<ListingResponseDto> listPublicActive(Long sellerUserId) {
    return listingRepo.findBySellerUserIdAndStatus(sellerUserId, ListingStatus.ACTIVE)
        .stream()
        .filter(l -> Boolean.TRUE.equals(l.getIsActive()))
        .map(l -> assembleResponse(l.getId()))
        .toList();
  }

  /* =============== UPDATE =============== */

  @Transactional
  public ListingResponseDto update(Long sellerUserId, Long id, ListingUpdateRequest req) {
    var e = load(id);
    ensureOwner(sellerUserId, e);

    var oldType = e.getType();
    mapper.updateEntity(e, req);

    // brand/series checks if changed
    if (req.getBrandId() != null) ensureBrandExists(e.getBrandId());
    if (req.getSeriesId() != null) ensureSeriesExists(e.getSeriesId());

    // SALE rule if type or price changed
    if (e.getType() == ListingType.SALE) {
      if (e.getPrice() == null || e.getPrice().compareTo(BigDecimal.ZERO) <= 0)
        throw new ValidationException("price must be > 0 for SALE");
      if (e.getCurrency() == null || e.getCurrency().isBlank())
        throw new ValidationException("currency is required for SALE");
    } else {
      e.setPrice(null);
      e.setCurrency(null);
    }

    // Images/Tags full replace if provided
    if (req.getImages() != null) upsertImagesInternal(id, req.getImages());
    if (req.getTagIds() != null) upsertTagsInternal(id, req.getTagIds());

    listingRepo.save(e);
    return assembleResponse(id);
  }

  /* =============== STATUS/MODERATION =============== */

  @Transactional
  public ListingResponseDto patchStatus(Long sellerUserId, Long id, String statusStr) {
    var e = load(id);
    ensureOwner(sellerUserId, e);
    if (statusStr == null || statusStr.isBlank()) throw new ValidationException("status is required");
    e.setStatus(ListingStatus.valueOf(statusStr.toUpperCase()));
    listingRepo.save(e);
    return assembleResponse(id);
  }

  @Transactional
  public ListingResponseDto moderate(Long id, Boolean isActive, String statusStr) {
    var e = load(id);
    if (isActive != null) e.setIsActive(isActive);
    if (statusStr != null && !statusStr.isBlank()) e.setStatus(ListingStatus.valueOf(statusStr.toUpperCase()));
    listingRepo.save(e);
    return assembleResponse(id);
  }

  /* =============== IMAGES/TAGS REPLACE APIs =============== */

  @Transactional
  public List<ListingImageDto> replaceImages(Long sellerUserId, Long id, List<ListingImageUpsertDto> images) {
    var e = load(id);
    ensureOwner(sellerUserId, e);
    return upsertImagesInternal(id, images);
  }

  @Transactional
  public List<TagDto> replaceTags(Long sellerUserId, Long id, List<Long> tagIds) {
    var e = load(id);
    ensureOwner(sellerUserId, e);
    return upsertTagsInternal(id, tagIds);
  }

  /* =============== DELETE =============== */

  @Transactional
  public void delete(Long sellerUserId, Long id) {
    var e = load(id);
    ensureOwner(sellerUserId, e);
    // önce child kayıtlar
    imageRepo.deleteByListingId(id);
    listingTagRepo.deleteByIdListingId(id);
    listingRepo.deleteById(id);
  }

  /* =============== HELPERS =============== */

  private Listing load(Long id) {
    return listingRepo.findById(id).orElseThrow(() -> new NotFoundException("Listing not found"));
  }

  private void ensureOwner(Long sellerUserId, Listing e) {
    if (!Objects.equals(e.getSellerUserId(), sellerUserId))
      throw new BusinessRuleException("not your listing");
  }

  private void ensureBrandExists(Long id) {
    brandRepo.findById(id).orElseThrow(() -> new ValidationException("brand not found: " + id));
  }

  private void ensureSeriesExists(Long id) {
    seriesRepo.findById(id).orElseThrow(() -> new ValidationException("series not found: " + id));
  }

  private void validateCreate(ListingCreateRequest req) {
    if (req == null) throw new ValidationException("body is required");
    if (req.getTitle() == null || req.getTitle().isBlank()) throw new ValidationException("title is required");
    if (req.getType() == null || req.getType().isBlank()) throw new ValidationException("type is required");
  }

  private List<ListingImageDto> upsertImagesInternal(Long listingId, List<ListingImageUpsertDto> images) {
    imageRepo.deleteByListingId(listingId);
    if (images == null || images.isEmpty()) return List.of();
    var entities = images.stream()
        .sorted(Comparator.comparingInt(ListingImageUpsertDto::getIdx))
        .map(i -> com.api.garagemint.garagemintapi.model.cars.ListingImage.builder()
            .listingId(listingId).url(i.getUrl().trim()).idx(i.getIdx()).build())
        .toList();
    return mapper.toImageDtoList(imageRepo.saveAll(entities));
  }

  private List<TagDto> upsertTagsInternal(Long listingId, List<Long> tagIds) {
    listingTagRepo.deleteByIdListingId(listingId);
    if (tagIds == null || tagIds.isEmpty()) return List.of();
    var uniqueIds = tagIds.stream().filter(Objects::nonNull).collect(Collectors.toCollection(LinkedHashSet::new));
    var tags = tagRepo.findByIdIn(new ArrayList<>(uniqueIds));
    var pairs = tags.stream()
        .map(t -> new ListingTag(new ListingTagId(listingId, t.getId())))
        .toList();
    listingTagRepo.saveAll(pairs);
    return mapper.toTagDtoList(tags);
  }

  private ListingResponseDto assembleResponse(Long id) {
    var e = load(id);
    var dto = mapper.toResponseDto(e);

    // images
    var images = imageRepo.findByListingIdOrderByIdxAsc(id);
    dto.setImages(mapper.toImageDtoList(images));

    // tags
    var lt = listingTagRepo.findByIdListingId(id);
    var tagIds = lt.stream().map(x -> x.getId().getTagId()).toList();
    var tags = tagRepo.findByIdIn(tagIds);
    dto.setTags(mapper.toTagDtoList(tags));

    // seller
    Profile seller = profileRepo.findByUserId(e.getSellerUserId()).orElse(null);
    if (seller != null) {
      dto.setSeller(new com.api.garagemint.garagemintapi.dto.cars.ListingSellerDto(
          seller.getUserId(), seller.getUsername(), seller.getDisplayName(),
          seller.getAvatarUrl(), seller.getLocation()
      ));
    }

    // brand/series names (opsiyonel enrich)
    if (e.getBrandId() != null) brandRepo.findById(e.getBrandId()).ifPresent(b -> dto.setBrandName(b.getName()));
    if (e.getSeriesId() != null) seriesRepo.findById(e.getSeriesId()).ifPresent(s -> dto.setSeriesName(s.getName()));

    return dto;
  }
}
