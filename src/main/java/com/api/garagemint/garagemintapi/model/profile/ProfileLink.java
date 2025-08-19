package com.api.garagemint.garagemintapi.model.profile;


import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name="profile_links",
        uniqueConstraints = @UniqueConstraint(name="ux_links_profile_idx", columnNames={"profile_id","idx"}),
        indexes = @Index(name="idx_links_profile", columnList="profile_id")
)
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class ProfileLink extends BaseTime {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name="profile_id", nullable=false)
    private Long profileId;

    @Enumerated(EnumType.STRING)
    @Column(nullable=false, length=24)
    private ProfileLinkType type;

    @Size(max=40) @Column(length=40)
    private String label;   // CUSTOM için opsiyonel başlık

    @NotBlank @Size(max=500) @Column(nullable=false, length=500)
    private String url;

    @Column(nullable=false)
    private Integer idx;

    @Builder.Default
    private boolean isPublic = true;
}
