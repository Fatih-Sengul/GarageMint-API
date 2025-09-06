package com.api.garagemint.garagemintapi.service.profile;

import com.api.garagemint.garagemintapi.dto.profile.ProfileOwnerDto;
import com.api.garagemint.garagemintapi.security.AuthUser;
import com.api.garagemint.garagemintapi.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = "application/json")
@RequiredArgsConstructor
@CrossOrigin(origins = {"http://localhost:3000","http://localhost:3001"}, allowCredentials = "true")
public class ProfileUploadController {

    private final FileStorageService storage;
    private final ProfileService profileService;

    @PostMapping(value = "/me/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileOwnerDto uploadAvatar(@AuthenticationPrincipal AuthUser me,
                                        @RequestPart("file") MultipartFile file) {
        String url = storage.saveImage(file, "avatars");
        return profileService.updateMyAvatar(me.id(), url);
    }

    @PostMapping(value = "/me/banner/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileOwnerDto uploadBanner(@AuthenticationPrincipal AuthUser me,
                                        @RequestPart("file") MultipartFile file) {
        String url = storage.saveImage(file, "banners");
        return profileService.updateMyBanner(me.id(), url);
    }
}
