package com.api.garagemint.garagemintapi.repository.cars;

import com.api.garagemint.garagemintapi.model.cars.ListingImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingImageRepository extends JpaRepository<ListingImage, Long> {
  List<ListingImage> findByListingIdOrderByIdxAsc(Long listingId);
  void deleteByListingId(Long listingId);
}
