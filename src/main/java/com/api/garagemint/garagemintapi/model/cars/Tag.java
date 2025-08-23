package com.api.garagemint.garagemintapi.model.cars;

import com.api.garagemint.garagemintapi.model.common.BaseTime;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags",
       indexes = {
         @Index(name="idx_tags_name", columnList = "name"),
         @Index(name="idx_tags_slug", columnList = "slug", unique = true)
       })
@Getter @Setter @Builder
@NoArgsConstructor @AllArgsConstructor
public class Tag extends BaseTime {

  @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable=false, length=80)
  private String name;

  @Column(nullable=false, length=80, unique=true)
  private String slug;
}

