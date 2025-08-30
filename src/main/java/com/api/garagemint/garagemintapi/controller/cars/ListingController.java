package com.api.garagemint.garagemintapi.controller.cars;

import com.api.garagemint.garagemintapi.dto.cars.*;
import com.api.garagemint.garagemintapi.service.cars.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping(value="/api/v1/cars/listings", produces="application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"}, allowCredentials = "true")

public class ListingController {

  private final ListingService listingService;

  // -------------------- PUBLIC --------------------

  @GetMapping("/{id}")
  public ListingResponseDto getPublic(@PathVariable Long id) {
    return listingService.getPublicById(id);
  }

  // GET /api/v1/cars/listings?brandIds=1,2&seriesIds=5&theme=JDM&priceMin=100&size=24
  @GetMapping
  public Page<ListingResponseDto> searchByQuery(
      @RequestParam(required = false) Long sellerUserId,
      @RequestParam(required = false) List<Long> brandIds,
      @RequestParam(required = false) List<Long> seriesIds,
      @RequestParam(required = false) List<Long> tagIds,
      @RequestParam(required = false) String theme,
      @RequestParam(required = false) String scale,
      @RequestParam(required = false) String condition,
      @RequestParam(required = false) Boolean limitedEdition,
      @RequestParam(required = false) String type,
      @RequestParam(required = false) String status,
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
    var f = ListingFilterRequest.builder()
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

  // POST /api/v1/cars/listings/search (body ile filtre)
  @PostMapping("/search")
  public Page<ListingResponseDto> search(@Valid @RequestBody ListingFilterRequest filter) {
    return listingService.search(filter);
  }

  // -------------------- OWNER (mock userId=1L) --------------------

  @PostMapping
  public ListingResponseDto create(@Valid @RequestBody ListingCreateRequest req) {
    Long currentUserId = 1L; // TODO: SecurityContext
    return listingService.create(currentUserId, req);
  }

  @GetMapping("/me/{id}")
  public ListingResponseDto getMine(@PathVariable Long id) {
    Long currentUserId = 1L;
    return listingService.getMyById(currentUserId, id);
  }

  @GetMapping("/me")
  public List<ListingResponseDto> listMyActive() {
    Long currentUserId = 1L;
    return listingService.listMyActive(currentUserId);
  }

  @PutMapping("/{id}")
  public ListingResponseDto update(@PathVariable Long id, @Valid @RequestBody ListingUpdateRequest req) {
    Long currentUserId = 1L;
    return listingService.update(currentUserId, id, req);
  }

  // Status değişimi (ACTIVE/SOLD/WITHDRAWN)
  @PatchMapping("/{id}/status")
  public ListingResponseDto patchStatus(@PathVariable Long id, @RequestParam String status) {
    Long currentUserId = 1L;
    return listingService.patchStatus(currentUserId, id, status);
  }

  // Medya & etiketleri replace et
  @PutMapping("/{id}/images")
  public List<ListingImageDto> replaceImages(@PathVariable Long id,
                                             @Valid @RequestBody List<ListingImageUpsertDto> images) {
    Long currentUserId = 1L;
    return listingService.replaceImages(currentUserId, id, images);
  }

  @PutMapping("/{id}/tags")
  public List<TagDto> replaceTags(@PathVariable Long id,
                                  @RequestBody List<Long> tagIds) {
    Long currentUserId = 1L;
    return listingService.replaceTags(currentUserId, id, tagIds);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    Long currentUserId = 1L;
    listingService.delete(currentUserId, id);
  }
}

