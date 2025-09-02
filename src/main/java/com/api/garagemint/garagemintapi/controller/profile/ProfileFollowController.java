package com.api.garagemint.garagemintapi.controller.profile;

import com.api.garagemint.garagemintapi.dto.profile.FollowListResponse;
import com.api.garagemint.garagemintapi.service.profile.ProfileFollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value="/api/v1/profiles", produces="application/json")
@RequiredArgsConstructor
public class ProfileFollowController {

  private final ProfileFollowService followService;

  // TODO: gerçek auth gelince meUserId SecurityContext'ten alınacak
  private Long meUserId() { return 1L; }

  @PostMapping("/{username}/follow")
  public void follow(@PathVariable String username) {
    followService.follow(meUserId(), username);
  }

  @DeleteMapping("/{username}/follow")
  public void unfollow(@PathVariable String username) {
    followService.unfollow(meUserId(), username);
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

