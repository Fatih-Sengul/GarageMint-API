package com.api.garagemint.garagemintapi.service.cars;

import com.api.garagemint.garagemintapi.dto.cars.BrandDto;
import com.api.garagemint.garagemintapi.mapper.cars.BrandMapper;
import com.api.garagemint.garagemintapi.model.cars.Brand;
import com.api.garagemint.garagemintapi.repository.cars.BrandRepository;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class BrandService {

  private final BrandRepository brandRepo;
  private final BrandMapper brandMapper;

  @Transactional
  public BrandDto create(BrandDto dto) {
    if (dto == null) throw new ValidationException("body is required");
    if (dto.getSlug() == null || dto.getSlug().isBlank()) throw new ValidationException("slug is required");
    if (brandRepo.existsBySlug(dto.getSlug())) throw new BusinessRuleException("slug already exists");
    var e = Brand.builder().name(dto.getName()).slug(dto.getSlug()).country(dto.getCountry()).build();
    return brandMapper.toDto(brandRepo.save(e));
  }

  @Transactional(readOnly = true)
  public BrandDto get(Long id) {
    return brandRepo.findById(id).map(brandMapper::toDto)
        .orElseThrow(() -> new NotFoundException("Brand not found"));
  }

  @Transactional(readOnly = true)
  public List<BrandDto> list() {
    return brandRepo.findAll().stream().map(brandMapper::toDto).toList();
  }

  @Transactional
  public BrandDto update(Long id, BrandDto dto) {
    var e = brandRepo.findById(id).orElseThrow(() -> new NotFoundException("Brand not found"));
    if (dto.getName() != null) e.setName(dto.getName());
    if (dto.getCountry() != null) e.setCountry(dto.getCountry());
    if (dto.getSlug() != null) {
      if (!dto.getSlug().equals(e.getSlug()) && brandRepo.existsBySlug(dto.getSlug()))
        throw new BusinessRuleException("slug already exists");
      e.setSlug(dto.getSlug());
    }
    return brandMapper.toDto(brandRepo.save(e));
  }

  @Transactional
  public void delete(Long id) {
    if (!brandRepo.existsById(id)) throw new NotFoundException("Brand not found");
    brandRepo.deleteById(id);
  }
}
