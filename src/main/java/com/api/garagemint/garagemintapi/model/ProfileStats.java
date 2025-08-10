package com.api.garagemint.garagemintapi.model;

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

    @Builder.Default private Integer itemsCount = 0;
    @Builder.Default private Integer listingsActiveCount = 0;
    @Builder.Default private Integer favoritesCount = 0;
    @Builder.Default private Integer followersCount = 0; // 2. sprint
    @Builder.Default private Short responseRate = 0;     // %
    private Instant lastActiveAt;
}
