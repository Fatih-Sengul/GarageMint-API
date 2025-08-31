package com.api.garagemint.garagemintapi.controller.cars;

import com.api.garagemint.garagemintapi.dto.cars.ListingModerationUpdateRequest;
import com.api.garagemint.garagemintapi.dto.cars.ListingResponseDto;
import com.api.garagemint.garagemintapi.service.cars.ListingService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api/v1/cars/admin/listings", produces="application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"}, allowCredentials = "true")
public class AdminListingController {

  private final ListingService listingService;

  // Moderasyon: ilanı gizle/göster veya status düzelt
  @PatchMapping("/{id}")
  public ListingResponseDto moderate(@PathVariable Long id,
                                     @Valid @RequestBody ListingModerationUpdateRequest req) {
    return listingService.moderate(id, req.getIsActive(), req.getStatus());
  }
}