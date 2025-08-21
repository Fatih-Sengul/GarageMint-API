package com.api.garagemint.garagemintapi.service.cars;

import com.api.garagemint.garagemintapi.dto.cars.SeriesDto;
import com.api.garagemint.garagemintapi.mapper.cars.SeriesMapper;
import com.api.garagemint.garagemintapi.model.cars.Series;
import com.api.garagemint.garagemintapi.repository.cars.SeriesRepository;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class SeriesService {

  private final SeriesRepository seriesRepo;
  private final SeriesMapper seriesMapper;

  @Transactional
  public SeriesDto create(SeriesDto dto) {
    if (dto == null) throw new ValidationException("body is required");
    if (dto.getBrandId() == null) throw new ValidationException("brandId is required");
    if (dto.getSlug() == null || dto.getSlug().isBlank()) throw new ValidationException("slug is required");
    if (seriesRepo.existsByBrandIdAndSlug(dto.getBrandId(), dto.getSlug()))
      throw new BusinessRuleException("slug already exists for this brand");
    var e = Series.builder().brandId(dto.getBrandId()).name(dto.getName()).slug(dto.getSlug()).build();
    return seriesMapper.toDto(seriesRepo.save(e));
  }

  @Transactional(readOnly = true)
  public SeriesDto get(Long id) {
    return seriesRepo.findById(id).map(seriesMapper::toDto)
        .orElseThrow(() -> new NotFoundException("Series not found"));
  }

  @Transactional(readOnly = true)
  public List<SeriesDto> listByBrand(Long brandId) {
    return seriesRepo.findByBrandId(brandId).stream().map(seriesMapper::toDto).toList();
  }

  @Transactional
  public SeriesDto update(Long id, SeriesDto dto) {
    var e = seriesRepo.findById(id).orElseThrow(() -> new NotFoundException("Series not found"));
    if (dto.getName() != null) e.setName(dto.getName());
    if (dto.getSlug() != null) {
      if (!dto.getSlug().equals(e.getSlug()) && seriesRepo.existsByBrandIdAndSlug(e.getBrandId(), dto.getSlug()))
        throw new BusinessRuleException("slug already exists for this brand");
      e.setSlug(dto.getSlug());
    }
    return seriesMapper.toDto(seriesRepo.save(e));
  }

  @Transactional
  public void delete(Long id) {
    if (!seriesRepo.existsById(id)) throw new NotFoundException("Series not found");
    seriesRepo.deleteById(id);
  }
}
