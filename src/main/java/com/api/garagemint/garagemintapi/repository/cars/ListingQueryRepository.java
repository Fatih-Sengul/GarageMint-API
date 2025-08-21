package com.api.garagemint.garagemintapi.repository.cars;

import com.api.garagemint.garagemintapi.dto.cars.ListingFilterRequest;
import com.api.garagemint.garagemintapi.model.cars.Listing;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ListingQueryRepository {
  Page<Listing> search(ListingFilterRequest filter, Pageable pageable);
}
