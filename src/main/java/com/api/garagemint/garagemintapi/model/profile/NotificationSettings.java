package com.api.garagemint.garagemintapi.model.profile;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="notification_settings")
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class NotificationSettings {

    @Id
    @Column(name="profile_id")
    private Long profileId;

    @MapsId
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="profile_id", foreignKey = @ForeignKey(name="fk_notif_profile"))
    private Profile profile;

    @Builder.Default private boolean emailGeneral = true;
    @Builder.Default private boolean emailMessage = true;
    @Builder.Default private boolean emailFavorite = true;
    @Builder.Default private boolean emailListingActivity = true;

    @Builder.Default private boolean pushGeneral = true;

    @Enumerated(EnumType.STRING)
    @Column(length=16, nullable=false)
    @Builder.Default
    private DigestFrequency digestFrequency = DigestFrequency.WEEKLY;
}
