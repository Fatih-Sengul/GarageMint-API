package com.api.garagemint.garagemintapi.model.profile;

import jakarta.persistence.Embeddable;
import lombok.*;
import java.io.Serializable;

@Embeddable
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @EqualsAndHashCode
public class FollowId implements Serializable {
  private Long followerId; // profile_id
  private Long followeeId; // profile_id
}
