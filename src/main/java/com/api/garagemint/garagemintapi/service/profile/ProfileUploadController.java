package com.api.garagemint.garagemintapi.service.profile;

import com.api.garagemint.garagemintapi.dto.profile.ProfileOwnerDto;
import com.api.garagemint.garagemintapi.security.SecurityUtil;
import com.api.garagemint.garagemintapi.service.storage.FileStorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(value = "/api/v1/profiles", produces = "application/json")
@RequiredArgsConstructor
public class ProfileUploadController {

    private final FileStorageService storage;
    private final ProfileService profileService;

    @PostMapping(value = "/me/avatar/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileOwnerDto uploadAvatar(@RequestPart("file") MultipartFile file) {
        Long uid = SecurityUtil.getCurrentUserId();
        String url = storage.saveImage(file, "avatars");
        return profileService.updateMyAvatar(uid, url);
    }

    @PostMapping(value = "/me/banner/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ProfileOwnerDto uploadBanner(@RequestPart("file") MultipartFile file) {
        Long uid = SecurityUtil.getCurrentUserId();
        String url = storage.saveImage(file, "banners");
        return profileService.updateMyBanner(uid, url);
    }
}
