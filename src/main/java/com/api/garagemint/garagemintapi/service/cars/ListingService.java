package com.api.garagemint.garagemintapi.service.cars;

import com.api.garagemint.garagemintapi.dto.cars.*;
import com.api.garagemint.garagemintapi.mapper.cars.ListingMapper;
import com.api.garagemint.garagemintapi.model.cars.*;
import com.api.garagemint.garagemintapi.model.profile.Profile;
import com.api.garagemint.garagemintapi.repository.cars.*;
import com.api.garagemint.garagemintapi.repository.profiles.ProfileRepository;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import jakarta.persistence.criteria.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ListingService {

  private static final int MAX_ACTIVE_LISTINGS_FREE = 3;

  private final ListingRepository listingRepo;
  private final ListingImageRepository imageRepo;
  private final ListingTagRepository listingTagRepo;
  private final TagRepository tagRepo;
  private final BrandRepository brandRepo;
  private final SeriesRepository seriesRepo;
  private final ProfileRepository profileRepo;
  private final ListingMapper mapper;

  /* ======================== CREATE ======================== */

  @Transactional
  public ListingResponseDto create(Long sellerUserId, ListingCreateRequest req) {
    validateCreate(req);

    long activeCount = listingRepo.countBySellerUserIdAndStatus(sellerUserId, ListingStatus.ACTIVE);
    if (activeCount >= MAX_ACTIVE_LISTINGS_FREE) {
      throw new BusinessRuleException("maximum active listings reached: " + MAX_ACTIVE_LISTINGS_FREE);
    }

    Listing e = mapper.toEntity(req);
    e.setSellerUserId(sellerUserId);
    e.setStatus(ListingStatus.ACTIVE);
    e.setIsActive(Boolean.TRUE);

    if (e.getType() == ListingType.SALE) {
      if (e.getPrice() == null || e.getPrice().compareTo(BigDecimal.ZERO) <= 0) {
        throw new ValidationException("price must be > 0 for SALE");
      }
      if (!StringUtils.hasText(req.getCurrency())) {
        throw new ValidationException("currency is required for SALE");
      }
      e.setCurrency(req.getCurrency().trim());
    } else {
      e.setPrice(null);
      e.setCurrency(null);
    }

    if (e.getBrandId() != null) ensureBrandExists(e.getBrandId());
    if (e.getSeriesId() != null) ensureSeriesExists(e.getSeriesId());

    Listing saved = listingRepo.save(e);

    upsertImagesInternal(saved.getId(), req.getImages());
    upsertTagsInternal(saved.getId(), req.getTagIds());

    return assembleResponse(saved.getId());
  }

  /* ========================= READ ========================= */

  @Transactional(readOnly = true)
  public ListingResponseDto getPublicById(Long id) {
    Listing e = load(id);
    if (!Boolean.TRUE.equals(e.getIsActive()) || e.getStatus() != ListingStatus.ACTIVE) {
      throw new NotFoundException("Listing not available");
    }
    return assembleResponse(id);
  }

  @Transactional(readOnly = true)
  public ListingResponseDto getMyById(Long sellerUserId, Long id) {
    Listing e = load(id);
    ensureOwner(sellerUserId, e);
    return assembleResponse(id);
  }

  @Transactional(readOnly = true)
  public List<ListingResponseDto> listMyActive(Long sellerUserId) {
    return listingRepo.findBySellerUserIdAndStatus(sellerUserId, ListingStatus.ACTIVE)
            .stream().map(l -> assembleResponse(l.getId())).toList();
  }

  @Transactional(readOnly = true)
  public List<ListingResponseDto> listPublicActive(Long sellerUserId) {
    return listingRepo.findBySellerUserIdAndStatus(sellerUserId, ListingStatus.ACTIVE)
            .stream().filter(l -> Boolean.TRUE.equals(l.getIsActive()))
            .map(l -> assembleResponse(l.getId())).toList();
  }

  /* ======================== SEARCH ======================== */

  @Transactional(readOnly = true)
  public Page<ListingResponseDto> search(ListingFilterRequest f) {
    Pageable pageable = buildPageable(f);
    Specification<Listing> spec = buildSpec(f);

    Page<Listing> page = listingRepo.findAll(spec, pageable);

    List<ListingResponseDto> dtos = page.getContent().stream()
            .map(e -> assembleResponse(e.getId()))
            .toList();

    return new PageImpl<>(dtos, pageable, page.getTotalElements());
  }

  private Specification<Listing> buildSpec(ListingFilterRequest f) {
    return (Root<Listing> root, CriteriaQuery<?> query, CriteriaBuilder cb) -> {
      List<Predicate> ps = new ArrayList<>();

      if (f.getSellerUserId() != null)
        ps.add(cb.equal(root.get("sellerUserId"), f.getSellerUserId()));

      if (!CollectionUtils.isEmpty(f.getBrandIds()))
        ps.add(root.get("brandId").in(f.getBrandIds()));

      if (!CollectionUtils.isEmpty(f.getSeriesIds()))
        ps.add(root.get("seriesId").in(f.getSeriesIds()));

      if (StringUtils.hasText(f.getTheme()))
        ps.add(cb.equal(root.get("theme"), f.getTheme()));

      if (StringUtils.hasText(f.getScale()))
        ps.add(cb.equal(root.get("scale"), f.getScale()));

      if (StringUtils.hasText(f.getCondition())) {
        Condition cond = safeEnum(Condition.class, f.getCondition());
        if (cond != null) ps.add(cb.equal(root.get("condition"), cond));
      }

      if (f.getLimitedEdition() != null)
        ps.add(cb.equal(root.get("limitedEdition"), f.getLimitedEdition()));

      if (StringUtils.hasText(f.getType())) {
        ListingType lt = safeEnum(ListingType.class, f.getType());
        if (lt != null) ps.add(cb.equal(root.get("type"), lt));
      }

      if (StringUtils.hasText(f.getStatus())) {
        ListingStatus st = safeEnum(ListingStatus.class, f.getStatus());
        if (st != null) ps.add(cb.equal(root.get("status"), st));
      }

      if (StringUtils.hasText(f.getLocation()))
        ps.add(cb.like(cb.lower(root.get("location")), "%" + f.getLocation().toLowerCase() + "%"));

      if (f.getModelYearFrom() != null)
        ps.add(cb.greaterThanOrEqualTo(root.get("modelYear"), f.getModelYearFrom()));
      if (f.getModelYearTo() != null)
        ps.add(cb.lessThanOrEqualTo(root.get("modelYear"), f.getModelYearTo()));

      if (f.getPriceMin() != null)
        ps.add(cb.greaterThanOrEqualTo(root.get("price"), f.getPriceMin()));
      if (f.getPriceMax() != null)
        ps.add(cb.lessThanOrEqualTo(root.get("price"), f.getPriceMax()));

      // TAG filtresi → EXISTS (ListingTag lt WHERE lt.id.listingId = root.id AND lt.id.tagId IN (:ids))
      if (!CollectionUtils.isEmpty(f.getTagIds())) {
        Subquery<Long> sq = query.subquery(Long.class);
        Root<ListingTag> lt = sq.from(ListingTag.class);
        Path<ListingTagId> id = lt.get("id");
        sq.select(cb.literal(1L))
                .where(
                        cb.equal(id.get("listingId"), root.get("id")),
                        id.get("tagId").in(f.getTagIds())
                );
        ps.add(cb.exists(sq));
      }

      return cb.and(ps.toArray(Predicate[]::new));
    };
  }

  private Pageable buildPageable(ListingFilterRequest f) {
    int page = Optional.ofNullable(f.getPage()).orElse(0);
    int size = Optional.ofNullable(f.getSize()).orElse(20);
    String sortBy = Optional.ofNullable(f.getSortBy()).orElse("createdAt");
    String sortDir = Optional.ofNullable(f.getSortDir()).orElse("DESC");

    Sort.Direction dir = "ASC".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
    String prop = switch (sortBy) {
      case "price"     -> "price";
      case "modelYear" -> "modelYear";
      default          -> "createdAt"; // BaseTime field
    };

    return PageRequest.of(page, size, Sort.by(dir, prop));
  }

  private static <E extends Enum<E>> E safeEnum(Class<E> e, String v) {
    if (!StringUtils.hasText(v)) return null;
    try { return Enum.valueOf(e, v.trim().toUpperCase()); }
    catch (IllegalArgumentException ex) { return null; }
  }

  /* ======================== UPDATE ======================== */

  @Transactional
  public ListingResponseDto update(Long sellerUserId, Long id, ListingUpdateRequest req) {
    Listing e = load(id);
    ensureOwner(sellerUserId, e);

    mapper.updateEntity(e, req);

    if (req.getBrandId() != null) ensureBrandExists(e.getBrandId());
    if (req.getSeriesId() != null) ensureSeriesExists(e.getSeriesId());

    if (e.getType() == ListingType.SALE) {
      if (e.getPrice() == null || e.getPrice().compareTo(BigDecimal.ZERO) <= 0)
        throw new ValidationException("price must be > 0 for SALE");
      if (!StringUtils.hasText(e.getCurrency()))
        throw new ValidationException("currency is required for SALE");
    } else {
      e.setPrice(null);
      e.setCurrency(null);
    }

    if (req.getImages() != null) upsertImagesInternal(id, req.getImages());
    if (req.getTagIds() != null) upsertTagsInternal(id, req.getTagIds());

    listingRepo.save(e);
    return assembleResponse(id);
  }

  /* ===================== STATUS / ADMIN ==================== */

  @Transactional
  public ListingResponseDto patchStatus(Long sellerUserId, Long id, String statusStr) {
    Listing e = load(id);
    ensureOwner(sellerUserId, e);
    ListingStatus st = safeEnum(ListingStatus.class, statusStr);
    if (st == null) throw new ValidationException("invalid status");
    e.setStatus(st);
    listingRepo.save(e);
    return assembleResponse(id);
  }

  @Transactional
  public ListingResponseDto moderate(Long id, Boolean isActive, String statusStr) {
    Listing e = load(id);
    if (isActive != null) e.setIsActive(isActive);
    ListingStatus st = safeEnum(ListingStatus.class, statusStr);
    if (st != null) e.setStatus(st);
    listingRepo.save(e);
    return assembleResponse(id);
  }

  /* ================== IMAGES / TAGS REPLACE ================= */

  @Transactional
  public List<ListingImageDto> replaceImages(Long sellerUserId, Long id, List<ListingImageUpsertDto> images) {
    Listing e = load(id);
    ensureOwner(sellerUserId, e);
    return upsertImagesInternal(id, images);
  }

  @Transactional
  public List<TagDto> replaceTags(Long sellerUserId, Long id, List<Long> tagIds) {
    Listing e = load(id);
    ensureOwner(sellerUserId, e);
    return upsertTagsInternal(id, tagIds);
  }

  /* ======================== DELETE ======================== */

  @Transactional
  public void delete(Long sellerUserId, Long id) {
    Listing e = load(id);
    ensureOwner(sellerUserId, e);
    imageRepo.deleteByListingId(id);
    listingTagRepo.deleteByIdListingId(id);
    listingRepo.deleteById(id);
  }

  /* ======================== HELPERS ======================== */

  private void validateCreate(ListingCreateRequest req) {
    if (req == null) throw new ValidationException("body is required");
    if (!StringUtils.hasText(req.getTitle())) throw new ValidationException("title is required");
    if (!StringUtils.hasText(req.getType())) throw new ValidationException("type is required");
  }

  private Listing load(Long id) {
    return listingRepo.findById(id).orElseThrow(() -> new NotFoundException("Listing not found"));
  }

  private void ensureOwner(Long sellerUserId, Listing e) {
    if (!Objects.equals(e.getSellerUserId(), sellerUserId)) {
      throw new BusinessRuleException("not your listing");
    }
  }

  private void ensureBrandExists(Long id) {
    brandRepo.findById(id).orElseThrow(() -> new ValidationException("brand not found: " + id));
  }

  private void ensureSeriesExists(Long id) {
    seriesRepo.findById(id).orElseThrow(() -> new ValidationException("series not found: " + id));
  }

  private List<ListingImageDto> upsertImagesInternal(Long listingId, List<ListingImageUpsertDto> images) {
    imageRepo.deleteByListingId(listingId);
    if (images == null || images.isEmpty()) return List.of();

    List<com.api.garagemint.garagemintapi.model.cars.ListingImage> entities = images.stream()
            .filter(Objects::nonNull)
            .sorted(Comparator.comparingInt(ListingImageUpsertDto::getIdx))
            .map(i -> com.api.garagemint.garagemintapi.model.cars.ListingImage.builder()
                    .listingId(listingId)
                    .url(i.getUrl().trim())
                    .idx(i.getIdx())
                    .build())
            .toList();

    return mapper.toImageDtoList(imageRepo.saveAll(entities));
  }

  private List<TagDto> upsertTagsInternal(Long listingId, List<Long> tagIds) {
    listingTagRepo.deleteByIdListingId(listingId);
    if (tagIds == null || tagIds.isEmpty()) return List.of();

    List<Long> uniqueIds = tagIds.stream().filter(Objects::nonNull)
            .collect(Collectors.toCollection(LinkedHashSet::new)).stream().toList();

    List<Tag> tags = tagRepo.findByIdIn(uniqueIds);
    List<ListingTag> pairs = tags.stream()
            .map(t -> new ListingTag(new ListingTagId(listingId, t.getId())))
            .toList();

    listingTagRepo.saveAll(pairs);
    return mapper.toTagDtoList(tags);
  }

  @Transactional(readOnly = true)
  public ListingResponseDto assembleResponse(Long id) {
    Listing e = load(id);
    ListingResponseDto dto = mapper.toResponseDto(e);

    // images
    var images = imageRepo.findByListingIdOrderByIdxAsc(id);
    dto.setImages(mapper.toImageDtoList(images));

    // tags
    var lt = listingTagRepo.findByIdListingId(id);
    var tagIds = lt.stream().map(x -> x.getId().getTagId()).toList();
    if (!tagIds.isEmpty()) {
      var tags = tagRepo.findByIdIn(tagIds);
      dto.setTags(mapper.toTagDtoList(tags));
    } else {
      dto.setTags(List.of());
    }

    // seller enrich
    Profile seller = profileRepo.findByUserId(e.getSellerUserId()).orElse(null);
    if (seller != null) {
      dto.setSeller(new ListingSellerDto(
              seller.getUserId(),
              seller.getUsername(),
              seller.getDisplayName(),
              seller.getAvatarUrl(),
              seller.getLocation()
      ));
    }

    // brand/series names
    if (e.getBrandId() != null) {
      brandRepo.findById(e.getBrandId()).ifPresent(b -> dto.setBrandName(b.getName()));
    }
    if (e.getSeriesId() != null) {
      seriesRepo.findById(e.getSeriesId()).ifPresent(s -> dto.setSeriesName(s.getName()));
    }

    return dto;
  }
}
