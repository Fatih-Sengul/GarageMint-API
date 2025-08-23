package com.api.garagemint.garagemintapi.model.profile;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="profile_prefs")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProfilePrefs {

    @Id
    @Column(name="profile_id")
    private Long profileId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profile_id", foreignKey=@ForeignKey(name="fk_prefs_profile"))
    private Profile profile;

    @Builder.Default private boolean showEmail = false;
    @Builder.Default private boolean showLocation = true;
    @Builder.Default private boolean showLinks = true;
    @Builder.Default private boolean searchable = true;
    @Builder.Default private boolean allowDm = true;
    @Builder.Default private boolean showCollection = true;
    @Builder.Default private boolean showListings = true;
}