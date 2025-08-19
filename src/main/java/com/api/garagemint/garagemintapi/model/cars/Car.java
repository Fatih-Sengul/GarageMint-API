package com.api.garagemint.garagemintapi.model.cars;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
class Car {
    @Id
    @GeneratedValue
    private UUID id;
    private String make;
    private String model;
    private int year;
    private String color;
    private BigDecimal estimatedValue;
    @ElementCollection
    private List<String> photos = new ArrayList<>();
    private boolean isForTrade;
}
