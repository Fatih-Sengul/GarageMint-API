package com.api.garagemint.garagemintapi.mapper.cars;

import com.api.garagemint.garagemintapi.dto.cars.TagDto;
import com.api.garagemint.garagemintapi.model.cars.Tag;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface TagMapper {
  TagDto toDto(Tag entity);
}

