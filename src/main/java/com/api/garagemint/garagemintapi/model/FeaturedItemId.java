package com.api.garagemint.garagemintapi.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class FeaturedItemId implements Serializable {
    private Long profileId;
    private Long itemId;
}