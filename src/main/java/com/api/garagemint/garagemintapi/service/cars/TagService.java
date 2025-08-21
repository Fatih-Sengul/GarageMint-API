package com.api.garagemint.garagemintapi.service.cars;

import com.api.garagemint.garagemintapi.dto.cars.TagDto;
import com.api.garagemint.garagemintapi.mapper.cars.TagMapper;
import com.api.garagemint.garagemintapi.model.cars.Tag;
import com.api.garagemint.garagemintapi.repository.cars.TagRepository;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service @RequiredArgsConstructor
public class TagService {

  private final TagRepository tagRepo;
  private final TagMapper tagMapper;

  @Transactional
  public TagDto create(TagDto dto) {
    if (dto == null) throw new ValidationException("body is required");
    if (dto.getSlug() == null || dto.getSlug().isBlank()) throw new ValidationException("slug is required");
    if (tagRepo.existsBySlug(dto.getSlug())) throw new BusinessRuleException("slug already exists");
    var e = Tag.builder().name(dto.getName()).slug(dto.getSlug()).build();
    return tagMapper.toDto(tagRepo.save(e));
  }

  @Transactional(readOnly = true)
  public TagDto get(Long id) {
    return tagRepo.findById(id).map(tagMapper::toDto)
        .orElseThrow(() -> new NotFoundException("Tag not found"));
  }

  @Transactional(readOnly = true)
  public List<TagDto> list() {
    return tagRepo.findAll().stream().map(tagMapper::toDto).toList();
  }

  @Transactional
  public TagDto update(Long id, TagDto dto) {
    var e = tagRepo.findById(id).orElseThrow(() -> new NotFoundException("Tag not found"));
    if (dto.getName() != null) e.setName(dto.getName());
    if (dto.getSlug() != null) {
      if (!dto.getSlug().equals(e.getSlug()) && tagRepo.existsBySlug(dto.getSlug()))
        throw new BusinessRuleException("slug already exists");
      e.setSlug(dto.getSlug());
    }
    return tagMapper.toDto(tagRepo.save(e));
  }

  @Transactional
  public void delete(Long id) {
    if (!tagRepo.existsById(id)) throw new NotFoundException("Tag not found");
    tagRepo.deleteById(id);
  }
}
