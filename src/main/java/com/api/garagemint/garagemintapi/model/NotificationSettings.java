package com.api.garagemint.garagemintapi.model;

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

    @Builder.Default private boolean emailGeneral = true;
    @Builder.Default private boolean emailMessage = true;
    @Builder.Default private boolean emailFavorite = true;
    @Builder.Default private boolean emailListingActivity = true;

    @Builder.Default private boolean pushGeneral = true; // 2. sprintte gerçek push
    @Builder.Default @Column(length=16) private String digestFrequency = "WEEKLY"; // OFF|DAILY|WEEKLY
}
