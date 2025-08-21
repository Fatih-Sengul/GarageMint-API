package com.api.garagemint.garagemintapi.mapper.cars;

import com.api.garagemint.garagemintapi.dto.cars.BrandDto;
import com.api.garagemint.garagemintapi.model.cars.Brand;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface BrandMapper {
  BrandDto toDto(Brand entity);
}

