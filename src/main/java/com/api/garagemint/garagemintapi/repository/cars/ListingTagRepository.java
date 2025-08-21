package com.api.garagemint.garagemintapi.repository.cars;

import com.api.garagemint.garagemintapi.model.cars.ListingTag;
import com.api.garagemint.garagemintapi.model.cars.ListingTagId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListingTagRepository extends JpaRepository<ListingTag, ListingTagId> {
  List<ListingTag> findByIdListingId(Long listingId);
  void deleteByIdListingId(Long listingId);
}
