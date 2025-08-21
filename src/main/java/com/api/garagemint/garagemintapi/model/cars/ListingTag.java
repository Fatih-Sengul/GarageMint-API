package com.api.garagemint.garagemintapi.model.cars;

import com.api.garagemint.garagemintapi.model.profile.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "listing_tags",
       indexes = {
         @Index(name="idx_listing_tags_listing", columnList = "listing_id"),
         @Index(name="idx_listing_tags_tag", columnList = "tag_id")
       })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ListingTag extends BaseTime {

  @EmbeddedId
  private ListingTagId id;
}

