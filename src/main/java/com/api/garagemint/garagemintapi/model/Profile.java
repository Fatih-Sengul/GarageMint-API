package com.api.garagemint.garagemintapi.model;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;
import jakarta.validation.constraints.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "profiles",
        indexes = {
                @Index(name="idx_profiles_public_verified", columnList = "is_public,is_verified"),
                @Index(name="idx_profiles_username", columnList = "username", unique = true)
        }
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Profile extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** users.id -> şimdilik ilişki kurmadan FK sütunu olarak tutuyoruz */
    @Column(name="user_id", nullable=false, unique = true)
    private Long userId;

    @Pattern(regexp = "^[a-z0-9_]{3,32}$")
    @Column(nullable=false, length=32, unique = true)
    private String username;

    @NotBlank @Size(max=80)
    @Column(name="display_name", nullable=false, length=80)
    private String displayName;

    @Size(max=500) @Column(length=500)
    private String bio;

    @Size(max=500) @Column(name="avatar_url", length=500)
    private String avatarUrl;

    @Size(max=500) @Column(name="banner_url", length=500)
    private String bannerUrl;

    @Size(max=120) @Column(length=120)
    private String location;

    @Size(max=250) @Column(name="website_url", length=250)
    private String websiteUrl;

    @Size(max=8) @Column(length=8)
    @Builder.Default
    private String language = "en";

    @Column(name="is_verified") @Builder.Default
    private boolean isVerified = false;

    @Column(name="is_public") @Builder.Default
    private boolean isPublic = true;
}