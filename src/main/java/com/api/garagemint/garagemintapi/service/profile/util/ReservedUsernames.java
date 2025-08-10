package com.api.garagemint.garagemintapi.service.profile.util;

import java.util.Set;

public final class ReservedUsernames {
  private ReservedUsernames() {}

  private static final Set<String> RESERVED = Set.of(
      "admin","root","system","support","help","api","auth","swagger","actuator",
      "security","login","logout","register","me","health","v3","docs"
  );

  public static boolean isReserved(String usernameLower) {
    return RESERVED.contains(usernameLower);
  }
}
