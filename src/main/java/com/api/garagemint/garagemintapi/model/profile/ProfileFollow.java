package com.api.garagemint.garagemintapi.model.profile;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "profile_follows",
    uniqueConstraints = @UniqueConstraint(name = "ux_follow_unique", columnNames = {"follower_profile_id","followed_profile_id"}),
    indexes = {
        @Index(name="idx_follow_follower", columnList = "follower_profile_id"),
        @Index(name="idx_follow_followed", columnList = "followed_profile_id")
    }
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileFollow extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name="follower_profile_id", nullable=false, foreignKey = @ForeignKey(name="fk_follow_follower"))
  private Profile follower;   // takip eden

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name="followed_profile_id", nullable=false, foreignKey = @ForeignKey(name="fk_follow_followed"))
  private Profile followed;   // takip edilen
}

