package com.api.garagemint.garagemintapi.mapper.cars;

import com.api.garagemint.garagemintapi.dto.cars.SeriesDto;
import com.api.garagemint.garagemintapi.model.cars.Series;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SeriesMapper {
  SeriesDto toDto(Series entity);
}

