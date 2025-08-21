package com.api.garagemint.garagemintapi.model.cars;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class ListingTagId implements Serializable {
  private Long listingId;
  private Long tagId;
}

