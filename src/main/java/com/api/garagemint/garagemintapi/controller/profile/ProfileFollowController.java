package com.api.garagemint.garagemintapi.controller.profile;

import com.api.garagemint.garagemintapi.dto.profile.FollowListResponse;
import com.api.garagemint.garagemintapi.security.AuthUser;
import com.api.garagemint.garagemintapi.service.profile.ProfileFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api/v1/profiles", produces="application/json")
@RequiredArgsConstructor
public class ProfileFollowController {

  private final ProfileFollowService followService;

  @PostMapping("/{username}/follow")
  public void follow(@PathVariable String username,
                     @AuthenticationPrincipal AuthUser me) {
    followService.follow(me.id(), username);
  }

  @DeleteMapping("/{username}/follow")
  public void unfollow(@PathVariable String username,
                       @AuthenticationPrincipal AuthUser me) {
    followService.unfollow(me.id(), username);
  }

  @GetMapping("/{username}/followers")
  public FollowListResponse followers(@PathVariable String username,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
    return followService.listFollowers(username, page, size);
  }

  @GetMapping("/{username}/following")
  public FollowListResponse following(@PathVariable String username,
                                      @RequestParam(defaultValue = "0") int page,
                                      @RequestParam(defaultValue = "20") int size) {
    return followService.listFollowing(username, page, size);
  }
}
