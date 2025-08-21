package com.api.garagemint.garagemintapi.repository.cars;

import com.api.garagemint.garagemintapi.model.cars.Brand;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface BrandRepository extends JpaRepository<Brand, Long> {
  Optional<Brand> findBySlug(String slug);
  boolean existsBySlug(String slug);
}
