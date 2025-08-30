package com.api.garagemint.garagemintapi.controller.profile;

import com.api.garagemint.garagemintapi.dto.profile.*;
import com.api.garagemint.garagemintapi.service.profile.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = "application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"}, allowCredentials = "true")
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

    // Kullanıcı adı önerileri (autocomplete)
    @GetMapping("/suggest-username")
    public UsernameSuggestionsDto suggestUsername(@RequestParam(required = false) String base) {
        return profileService.suggestUsernames(base);
    }

    // ---- Owner Endpoints (mock userId = 1L) ----
    // TODO: Gerçekte userId SecurityContext'ten alınacak

    // Profil yoksa oluştur + getir (idempotent)
    @PostMapping("/me/init")
    public ProfileOwnerDto initMyProfile() {
        return profileService.ensureMyProfile(1L);
    }

    @GetMapping("/me")
    public ProfileOwnerDto getMyProfile() {
        return profileService.getMyProfile(1L);
    }

    @PutMapping("/me")
    public ProfileOwnerDto updateMyProfile(@Valid @RequestBody ProfileUpdateRequest req) {
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
    public List<ProfileLinkDto> updateMyLinks(@RequestBody List<@Valid ProfileLinkDto> links) {
        return profileService.upsertMyLinks(1L, links);
    }

    @PutMapping("/me/prefs")
    public ProfilePrefsDto updateMyPrefs(@Valid @RequestBody ProfilePrefsUpdateRequest req) {
        return profileService.updateMyPrefs(1L, req);
    }

    @PutMapping("/me/notifications")
    public NotificationSettingsDto updateMyNotifications(@Valid @RequestBody NotificationSettingsUpdateRequest req) {
        return profileService.updateMyNotificationSettings(1L, req);
    }
}

