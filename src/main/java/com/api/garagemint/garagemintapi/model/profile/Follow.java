package com.api.garagemint.garagemintapi.model.profile;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name="follows",
       uniqueConstraints = @UniqueConstraint(name="ux_follow_pair", columnNames={"follower_id","followee_id"}),
       indexes = {
         @Index(name="idx_follow_follower", columnList="follower_id"),
         @Index(name="idx_follow_followee", columnList="followee_id")
       })
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Follow extends BaseTime {

  @EmbeddedId
  private FollowId id;

  @MapsId("followerId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="follower_id", nullable=false, foreignKey=@ForeignKey(name="fk_follow_follower"))
  private Profile follower;

  @MapsId("followeeId")
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name="followee_id", nullable=false, foreignKey=@ForeignKey(name="fk_follow_followee"))
  private Profile followee;
}
