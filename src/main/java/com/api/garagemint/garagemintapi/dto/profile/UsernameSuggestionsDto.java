package com.api.garagemint.garagemintapi.dto.profile;

import lombok.*;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UsernameSuggestionsDto {
  private List<String> candidates;
}
