package com.api.garagemint.garagemintapi.controller.profile;

import com.api.garagemint.garagemintapi.dto.profile.*;
import com.api.garagemint.garagemintapi.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    // ---- Public Endpoints ----

    @GetMapping("/{username}")
    public ProfilePublicDto getPublicProfile(@PathVariable String username) {
        return profileService.getPublicProfileByUsername(username);
    }

    @GetMapping("/check-username")
    public UsernameAvailabilityDto checkUsername(@RequestParam String username) {
        return profileService.checkUsernameAvailability(username);
    }

    // ---- Owner Endpoints (mock userId = 1L) ----
    // Gerçekte userId SecurityContext'ten alınacak

    @GetMapping("/me")
    public ProfileOwnerDto getMyProfile() {
        return profileService.getMyProfile(1L);
    }

    @PutMapping("/me")
    public ProfileOwnerDto updateMyProfile(@RequestBody ProfileUpdateRequest req) {
        return profileService.updateMyProfile(1L, req);
    }

    @PutMapping("/me/avatar")
    public ProfileOwnerDto updateMyAvatar(@RequestParam String avatarUrl) {
        return profileService.updateMyAvatar(1L, avatarUrl);
    }

    @PutMapping("/me/banner")
    public ProfileOwnerDto updateMyBanner(@RequestParam String bannerUrl) {
        return profileService.updateMyBanner(1L, bannerUrl);
    }

    @PutMapping("/me/links")
    public List<ProfileLinkDto> updateMyLinks(@RequestBody List<ProfileLinkDto> links) {
        return profileService.upsertMyLinks(1L, links);
    }

    @PutMapping("/me/prefs")
    public ProfilePrefsDto updateMyPrefs(@RequestBody ProfilePrefsUpdateRequest req) {
        return profileService.updateMyPrefs(1L, req);
    }

    @PutMapping("/me/notifications")
    public NotificationSettingsDto updateMyNotifications(@RequestBody NotificationSettingsUpdateRequest req) {
        return profileService.updateMyNotificationSettings(1L, req);
    }

    @PutMapping("/me/featured")
    public List<ProfileFeaturedItemDto> updateMyFeatured(@RequestBody List<ProfileFeaturedItemDto> items) {
        return profileService.updateMyFeaturedItems(1L, items);
    }
}

