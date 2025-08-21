package com.api.garagemint.garagemintapi.repository.cars;

import com.api.garagemint.garagemintapi.model.cars.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TagRepository extends JpaRepository<Tag, Long> {
  List<Tag> findByIdIn(List<Long> ids);
  boolean existsBySlug(String slug);
}
