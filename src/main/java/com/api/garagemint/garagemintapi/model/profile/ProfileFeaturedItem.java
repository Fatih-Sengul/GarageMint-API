package com.api.garagemint.garagemintapi.model.profile;


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

    @MapsId("profileId")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profile_id", foreignKey=@ForeignKey(name="fk_featured_profile"))
    private Profile profile;

    /** 0..8 arası: vitrin sırası */
    @Column(nullable=false)
    private Integer idx;
}
