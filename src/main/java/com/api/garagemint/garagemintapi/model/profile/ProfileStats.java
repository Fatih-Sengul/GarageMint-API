package com.api.garagemint.garagemintapi.model.profile;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Entity
@Table(name="profile_stats")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProfileStats {

    @Id
    @Column(name="profile_id")
    private Long profileId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profile_id", foreignKey=@ForeignKey(name="fk_stats_profile"))
    private Profile profile;

    /* Listing odaklı sayaçlar */
    @Builder.Default private Integer listingsActiveCount = 0;
    @Builder.Default private Integer listingsTotalCount  = 0;

    /* Sosyal metrikler */
    @Builder.Default private Integer followersCount = 0;
    @Builder.Default private Integer followingCount = 0;
    @Builder.Default private Short   responseRate    = 0;     // %
    private Instant lastActiveAt;
}
