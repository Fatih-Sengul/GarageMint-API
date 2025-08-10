package com.api.garagemint.garagemintapi.service.profile.validator;

import com.api.garagemint.garagemintapi.model.ProfileLinkType;

import java.net.URI;
import java.util.Set;

public class LinkValidator {

  public static void validate(ProfileLinkType type, String url) {
    if (url == null || url.isBlank()) throw new IllegalArgumentException("url is blank");
    URI u = URI.create(url.trim());
    String host = u.getHost() == null ? "" : u.getHost().toLowerCase();

    switch (type) {
      case INSTAGRAM -> mustContainHost(host, Set.of("instagram.com"));
      case X -> mustContainHost(host, Set.of("x.com","twitter.com"));
      case YOUTUBE -> mustContainHost(host, Set.of("youtube.com","youtu.be"));
      case TIKTOK -> mustContainHost(host, Set.of("tiktok.com"));
      case THREADS -> mustContainHost(host, Set.of("threads.net"));
      case REDDIT -> mustContainHost(host, Set.of("reddit.com"));
      case CUSTOM -> { /* allow any http/https */ }
      default -> throw new IllegalArgumentException("Unsupported link type: " + type);
    }

    String scheme = u.getScheme();
    if (scheme == null || !(scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https"))) {
      throw new IllegalArgumentException("url must start with http/https");
    }
  }

  private static void mustContainHost(String host, Set<String> allowed) {
    boolean ok = allowed.stream().anyMatch(host::endsWith);
    if (!ok) throw new IllegalArgumentException("url host not allowed: " + host);
  }
}
