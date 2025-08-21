package com.api.garagemint.garagemintapi.repository.cars;

import com.api.garagemint.garagemintapi.model.cars.Series;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SeriesRepository extends JpaRepository<Series, Long> {
  List<Series> findByBrandId(Long brandId);
  boolean existsByBrandIdAndSlug(Long brandId, String slug);
}
