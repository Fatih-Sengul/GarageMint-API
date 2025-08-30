package com.api.garagemint.garagemintapi.controller.cars;

import com.api.garagemint.garagemintapi.dto.cars.BrandDto;
import com.api.garagemint.garagemintapi.service.cars.BrandService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/cars/brands", produces="application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"}, allowCredentials = "true")
public class BrandController {

  private final BrandService brandService;

  @PostMapping
  public BrandDto create(@Valid @RequestBody BrandDto dto) {
    return brandService.create(dto);
  }

  @GetMapping("/{id}")
  public BrandDto get(@PathVariable Long id) {
    return brandService.get(id);
  }

  @GetMapping
  public List<BrandDto> list() {
    return brandService.list();
  }

  @PutMapping("/{id}")
  public BrandDto update(@PathVariable Long id, @Valid @RequestBody BrandDto dto) {
    return brandService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    brandService.delete(id);
  }
}

