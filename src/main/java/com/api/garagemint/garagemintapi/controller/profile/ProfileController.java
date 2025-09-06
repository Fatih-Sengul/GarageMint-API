package com.api.garagemint.garagemintapi.controller.profile;

import com.api.garagemint.garagemintapi.dto.profile.*;
import com.api.garagemint.garagemintapi.security.AuthUser;
import com.api.garagemint.garagemintapi.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = "application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"}, allowCredentials = "true")
public class ProfileController {

    private final ProfileService profileService;

    // ---- Public Endpoints ----

    @GetMapping("/{username}")
    public ProfilePublicDto getPublicProfile(@PathVariable String username,
                                             @AuthenticationPrincipal AuthUser viewer) {
        Long viewerUserId = viewer != null ? viewer.id() : null;
        return profileService.getPublicProfileByUsername(username, viewerUserId);
    }

    @GetMapping("/check-username")
    public UsernameAvailabilityDto checkUsername(@RequestParam String username) {
        return profileService.checkUsernameAvailability(username);
    }

    @GetMapping("/suggest-username")
    public UsernameSuggestionsDto suggestUsername(@RequestParam(required = false) String base) {
        return profileService.suggestUsernames(base);
    }

    // ---- Owner Endpoints ----

    @Deprecated
    @PostMapping("/me/init")
    public ProfileOwnerDto initMyProfile(@AuthenticationPrincipal AuthUser me) {
        return profileService.ensureMyProfile(me.id());
    }

    @GetMapping("/me")
    public ProfileOwnerDto getMyProfile(@AuthenticationPrincipal AuthUser me) {
        return profileService.ensureMyProfile(me.id());
    }

    @PutMapping("/me")
    public ProfileOwnerDto updateMyProfile(@AuthenticationPrincipal AuthUser me,
                                           @Valid @RequestBody ProfileUpdateRequest req) {
        return profileService.updateMyProfile(me.id(), req);
    }

    @PutMapping("/me/avatar")
    public ProfileOwnerDto updateMyAvatar(@AuthenticationPrincipal AuthUser me,
                                          @RequestParam String avatarUrl) {
        return profileService.updateMyAvatar(me.id(), avatarUrl);
    }

    @PutMapping("/me/banner")
    public ProfileOwnerDto updateMyBanner(@AuthenticationPrincipal AuthUser me,
                                          @RequestParam String bannerUrl) {
        return profileService.updateMyBanner(me.id(), bannerUrl);
    }

    @PutMapping("/me/links")
    public List<ProfileLinkDto> updateMyLinks(@AuthenticationPrincipal AuthUser me,
                                              @RequestBody List<@Valid ProfileLinkDto> links) {
        return profileService.upsertMyLinks(me.id(), links);
    }

    @PutMapping("/me/prefs")
    public ProfilePrefsDto updateMyPrefs(@AuthenticationPrincipal AuthUser me,
                                         @Valid @RequestBody ProfilePrefsUpdateRequest req) {
        return profileService.updateMyPrefs(me.id(), req);
    }

    @PutMapping("/me/notifications")
    public NotificationSettingsDto updateMyNotifications(
            @AuthenticationPrincipal AuthUser me,
            @Valid @RequestBody NotificationSettingsUpdateRequest req) {
        return profileService.updateMyNotificationSettings(me.id(), req);
    }
}
