package com.api.garagemint.garagemintapi.model;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="profile_featured_items",
        uniqueConstraints = @UniqueConstraint(name="ux_featured_idx", columnNames = {"profile_id","idx"}),
        indexes = @Index(name="idx_featured_profile", columnList = "profile_id")
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProfileFeaturedItem {

    @EmbeddedId
    private FeaturedItemId id;

    /** 0..8 arası: vitrin sırası */
    @Column(nullable=false)
    private Integer idx;
}
