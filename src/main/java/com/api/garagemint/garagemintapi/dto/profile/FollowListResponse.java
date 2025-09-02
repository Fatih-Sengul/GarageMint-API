package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class FollowListResponse {
  private List<FollowUserDto> items;
  private int page;
  private int size;
  private long totalElements;
  private int totalPages;
}

