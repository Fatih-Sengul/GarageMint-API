package com.api.garagemint.garagemintapi.model.profile;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
    name = "profile_follows",
    uniqueConstraints = @UniqueConstraint(columnNames = {"follower_profile_id", "followed_profile_id"})
)
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ProfileFollow extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "follower_profile_id", nullable = false)
  private Profile follower;   // takip eden

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "followed_profile_id", nullable = false)
  private Profile followed;   // takip edilen
}

