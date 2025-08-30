package com.api.garagemint.garagemintapi.controller.cars;

import com.api.garagemint.garagemintapi.dto.cars.SeriesDto;
import com.api.garagemint.garagemintapi.service.cars.SeriesService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(value="/api/v1/cars/series", produces="application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"}, allowCredentials = "true")

public class SeriesController {

  private final SeriesService seriesService;

  @PostMapping
  public SeriesDto create(@Valid @RequestBody SeriesDto dto) {
    return seriesService.create(dto);
  }

  @GetMapping("/{id}")
  public SeriesDto get(@PathVariable Long id) {
    return seriesService.get(id);
  }

  // /api/v1/cars/series?brandId=1
  @GetMapping
  public List<SeriesDto> listByBrand(@RequestParam Long brandId) {
    return seriesService.listByBrand(brandId);
  }

  @PutMapping("/{id}")
  public SeriesDto update(@PathVariable Long id, @Valid @RequestBody SeriesDto dto) {
    return seriesService.update(id, dto);
  }

  @DeleteMapping("/{id}")
  public void delete(@PathVariable Long id) {
    seriesService.delete(id);
  }
}

