package com.api.garagemint.garagemintapi.controller.profile;

import com.api.garagemint.garagemintapi.dto.profile.*;
import com.api.garagemint.garagemintapi.security.AuthUser;
import com.api.garagemint.garagemintapi.security.SecurityUtil;
import com.api.garagemint.garagemintapi.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = "application/json")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ---- Public Endpoints ----

    @GetMapping("/{username}")
    public ProfilePublicDto getPublicProfile(@PathVariable String username) {
        Long viewerUserId = SecurityUtil.getCurrentUserId();
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
    public ProfileOwnerDto updateMyProfile(@Valid @RequestBody ProfileUpdateRequest req) {
        Long uid = SecurityUtil.getCurrentUserId();
        return profileService.updateMyProfile(uid, req);
    }

    @PutMapping("/me/avatar")
    public ProfileOwnerDto updateMyAvatar(@RequestParam String avatarUrl) {
        Long uid = SecurityUtil.getCurrentUserId();
        return profileService.updateMyAvatar(uid, avatarUrl);
    }

    @PutMapping("/me/banner")
    public ProfileOwnerDto updateMyBanner(@RequestParam String bannerUrl) {
        Long uid = SecurityUtil.getCurrentUserId();
        return profileService.updateMyBanner(uid, bannerUrl);
    }

    @PutMapping("/me/links")
    public List<ProfileLinkDto> updateMyLinks(@RequestBody List<@Valid ProfileLinkDto> links) {
        Long uid = SecurityUtil.getCurrentUserId();
        return profileService.upsertMyLinks(uid, links);
    }

    @PutMapping("/me/prefs")
    public ProfilePrefsDto updateMyPrefs(@Valid @RequestBody ProfilePrefsUpdateRequest req) {
        Long uid = SecurityUtil.getCurrentUserId();
        return profileService.updateMyPrefs(uid, req);
    }

    @PutMapping("/me/notifications")
    public NotificationSettingsDto updateMyNotifications(@Valid @RequestBody NotificationSettingsUpdateRequest req) {
        Long uid = SecurityUtil.getCurrentUserId();
        return profileService.updateMyNotificationSettings(uid, req);
    }
}
