package com.api.garagemint.garagemintapi.repository.cars;

import com.api.garagemint.garagemintapi.model.cars.Listing;
import com.api.garagemint.garagemintapi.model.cars.ListingStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingRepository extends JpaRepository<Listing, Long> {
  long countBySellerUserIdAndStatus(Long sellerUserId, ListingStatus status);
  List<Listing> findBySellerUserIdAndStatus(Long sellerUserId, ListingStatus status);
}
