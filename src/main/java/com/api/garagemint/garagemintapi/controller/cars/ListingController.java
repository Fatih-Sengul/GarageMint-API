package com.api.garagemint.garagemintapi.controller.cars;

import com.api.garagemint.garagemintapi.dto.cars.*;
import com.api.garagemint.garagemintapi.security.AuthUser;
import com.api.garagemint.garagemintapi.service.cars.ListingService;
import com.api.garagemint.garagemintapi.model.cars.Condition;
import com.api.garagemint.garagemintapi.model.cars.ListingType;
import com.api.garagemint.garagemintapi.model.cars.ListingStatus;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1/listings", produces="application/json")
@RequiredArgsConstructor
public class ListingController {

  private final ListingService listingService;

  // -------------------- PUBLIC --------------------

  @GetMapping("/{id}")
  public ListingResponseDto getPublic(@PathVariable Long id) {
    return listingService.getPublicById(id);
  }

  @GetMapping
  public Page<ListingResponseDto> searchByQuery(
      @RequestParam(required = false) Long sellerUserId,
      @RequestParam(required = false) List<Long> brandIds,
      @RequestParam(required = false) List<Long> seriesIds,
      @RequestParam(required = false) List<Long> tagIds,
      @RequestParam(required = false) String theme,
      @RequestParam(required = false) String scale,
      @RequestParam(required = false) Condition condition,
      @RequestParam(required = false) Boolean limitedEdition,
      @RequestParam(required = false) ListingType type,
      @RequestParam(required = false) ListingStatus status,
      @RequestParam(required = false) String location,
      @RequestParam(required = false) Short modelYearFrom,
      @RequestParam(required = false) Short modelYearTo,
      @RequestParam(required = false) BigDecimal priceMin,
      @RequestParam(required = false) BigDecimal priceMax,
      @RequestParam(defaultValue = "0") Integer page,
      @RequestParam(defaultValue = "20") Integer size,
      @RequestParam(defaultValue = "createdAt") String sortBy,
      @RequestParam(defaultValue = "DESC") String sortDir
  ) {
    ListingFilterRequest f = ListingFilterRequest.builder()
        .sellerUserId(sellerUserId)
        .brandIds(brandIds)
        .seriesIds(seriesIds)
        .tagIds(tagIds)
        .theme(theme)
        .scale(scale)
        .condition(condition)
        .limitedEdition(limitedEdition)
        .type(type)
        .status(status)
        .location(location)
        .modelYearFrom(modelYearFrom)
        .modelYearTo(modelYearTo)
        .priceMin(priceMin)
        .priceMax(priceMax)
        .page(page)
        .size(size)
        .sortBy(sortBy)
        .sortDir(sortDir)
        .build();
    return listingService.search(f);
  }

  @PostMapping("/search")
  public Page<ListingResponseDto> search(@Valid @RequestBody ListingFilterRequest filter) {
    return listingService.search(filter);
  }

  // -------------------- OWNER --------------------

  @PostMapping
  public ListingResponseDto create(
      @AuthenticationPrincipal AuthUser user,
      @Valid @RequestBody ListingCreateRequest req) {
    return listingService.create(user.id(), req);
  }

  @GetMapping("/me/{id}")
  public ListingResponseDto getMine(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id) {
    return listingService.getMyById(user.id(), id);
  }

  @GetMapping("/me")
  public Page<ListingResponseDto> listMine(
      @AuthenticationPrincipal AuthUser user,
      @RequestParam(defaultValue = "0") int page,
      @RequestParam(defaultValue = "20") int size) {
    return listingService.listMy(user.id(), page, size);
  }

  @PutMapping("/{id}")
  public ListingResponseDto update(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id,
      @Valid @RequestBody ListingUpdateRequest req) {
    return listingService.updateListing(user.id(), id, req);
  }

  @PatchMapping("/{id}/status")
  public ListingResponseDto patchStatus(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id,
      @RequestParam ListingStatus status) {
    return listingService.patchStatus(user.id(), id, status);

  }

  @PutMapping("/{id}/images")
  public List<ListingImageDto> replaceImages(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id,
      @Valid @RequestBody List<ListingImageUpsertDto> images) {
    return listingService.replaceImages(user.id(), id, images);
  }

  @PutMapping("/{id}/tags")
  public List<TagDto> replaceTags(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id,
      @RequestBody List<Long> tagIds) {
    return listingService.replaceTags(user.id(), id, tagIds);
  }

  @DeleteMapping("/{id}")
  public void delete(
      @AuthenticationPrincipal AuthUser user,
      @PathVariable Long id) {
    listingService.deleteListing(user.id(), id);
  }
}
