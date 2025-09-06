package com.api.garagemint.garagemintapi.controller.cars;

import com.api.garagemint.garagemintapi.dto.cars.TagDto;
import com.api.garagemint.garagemintapi.service.cars.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/cars/tags", produces="application/json")
@RequiredArgsConstructor

public class TagController {

  private final TagService tagService;

  @PostMapping
  public TagDto create(@Valid @RequestBody TagDto dto) {
    return tagService.create(dto);
  }

  @GetMapping("/{id}")
  public TagDto get(@PathVariable Long id) {
    return tagService.get(id);
  }

  @GetMapping
  public List<TagDto> list() {
    return tagService.list();
  }

  @PutMapping("/{id}")
  public TagDto update(@PathVariable Long id, @Valid @RequestBody TagDto dto) {
    return tagService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    tagService.delete(id);
  }
}

