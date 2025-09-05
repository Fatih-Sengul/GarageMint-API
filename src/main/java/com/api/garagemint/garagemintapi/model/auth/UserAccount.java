package com.api.garagemint.garagemintapi.model.auth;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="users",
  indexes={
    @Index(name="idx_users_email_unique", columnList="email", unique=true),
    @Index(name="idx_users_username_unique", columnList="username", unique=true)
  }
)
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserAccount extends BaseTime {

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, length=120, unique=true)
  private String email;

  @Column(nullable=false, length=60)
  private String password;

  @Column(nullable=false, length=32, unique=true)
  private String username;

  @Column(nullable=false)
  private boolean enabled;

  @Enumerated(EnumType.STRING)
  @Column(nullable=false, length=16)
  @Builder.Default
  private UserRole role = UserRole.USER;
}
