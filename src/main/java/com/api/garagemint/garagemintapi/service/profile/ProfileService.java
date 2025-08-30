package com.api.garagemint.garagemintapi.service.profile;

import com.api.garagemint.garagemintapi.dto.profile.*;
import com.api.garagemint.garagemintapi.mapper.profile.ProfileMapper;
import com.api.garagemint.garagemintapi.model.profile.*;
import com.api.garagemint.garagemintapi.repository.*;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import com.api.garagemint.garagemintapi.service.cars.ListingService;
import com.api.garagemint.garagemintapi.service.profile.util.ReservedUsernames;
import com.api.garagemint.garagemintapi.service.profile.validator.LinkValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProfileService {

  private final ProfileRepository profileRepo;
  private final ProfileLinkRepository linkRepo;
  private final ProfilePrefsRepository prefsRepo;
  private final NotificationSettingsRepository notifRepo;
  private final ProfileStatsRepository statsRepo;
  private final ProfileMapper mapper;
  private final ListingService listingService;

  /* -------------------- PUBLIC -------------------- */

  @Transactional(readOnly = true)
  public ProfilePublicDto getPublicProfileByUsername(String username) {
    if (username == null || username.isBlank()) throw new ValidationException("username is required");
    var p = profileRepo.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new NotFoundException("Profile not found"));

    var dto = mapper.toPublicDto(p);

    if (!p.isPublic()) {
      dto.setBio(null);
      dto.setBannerUrl(null);
      dto.setLocation(null);
      dto.setWebsiteUrl(null);
      dto.setLinks(List.of());
      dto.setStats(null);
      dto.setListings(List.of());
      return dto;
    }

    var links = linkRepo.findByProfile_IdAndIsPublicTrueOrderByIdxAsc(p.getId());
    dto.setLinks(mapper.toLinkDtoList(links));

    var stats = statsRepo.findById(p.getId())
        .orElseGet(() -> ProfileStats.builder().profileId(p.getId()).build());
    dto.setStats(mapper.toDto(stats));

    var listings = listingService.listPublicActive(p.getUserId());
    dto.setListings(listings);

    return dto;
  }

  @Transactional(readOnly = true)
  public UsernameAvailabilityDto checkUsernameAvailability(String username) {
    var result = new UsernameAvailabilityDto(false);
    if (username == null) return result;
    var u = username.trim().toLowerCase();
    if (!u.matches("^[a-z0-9_]{3,32}$")) return result;
    if (ReservedUsernames.isReserved(u)) return result;
    result.setAvailable(!profileRepo.existsByUsernameIgnoreCase(u));
    return result;
  }

  @Transactional(readOnly = true)
  public UsernameSuggestionsDto suggestUsernames(String base) {
    String seed = (base == null ? "user" : base.trim().toLowerCase()).replaceAll("[^a-z0-9_]", "_");
    if (seed.length() < 3) seed = seed + "123";
    if (seed.length() > 16) seed = seed.substring(0, 16);

    ArrayList<String> list = new ArrayList<>();

    if (seed.matches("^[a-z0-9_]{3,32}$")
        && !ReservedUsernames.isReserved(seed)
        && !profileRepo.existsByUsernameIgnoreCase(seed)) {
      list.add(seed);
    }

    for (int i = 1; list.size() < 5 && i <= 99; i++) {
      String u = seed + i;
      if (!profileRepo.existsByUsernameIgnoreCase(u)) list.add(u);
    }

    return UsernameSuggestionsDto.builder()
        .candidates(list).build();
  }

  /* -------------------- OWNER -------------------- */

  @Transactional
  public ProfileOwnerDto ensureMyProfile(Long userId) {
    return profileRepo.findByUserId(userId)
        .map(p -> getMyProfile(userId))
        .orElseGet(() -> {
          String u = generateUniqueUsername("user" + userId);
          var p = Profile.builder()
              .userId(userId)
              .username(u)
              .displayName("User " + userId)
              .language("en")
              .isPublic(true)
              .build();
          p = profileRepo.save(p);

          prefsRepo.save(ProfilePrefs.builder().profile(p).build());
          notifRepo.save(NotificationSettings.builder().profile(p).build());
          statsRepo.save(ProfileStats.builder().profile(p).build());

          return getMyProfile(userId);
        });
  }

  @Transactional(readOnly = true)
  public ProfileOwnerDto getMyProfile(Long userId) {
    var p = loadByUserId(userId);
    var ownerDto = mapper.toOwnerDto(p);

    var links = linkRepo.findByProfile_IdOrderByIdxAsc(p.getId());
    ownerDto.setLinks(mapper.toLinkDtoList(links));

    var prefs = prefsRepo.findById(p.getId()).orElseGet(() -> defaultsPrefs(p));
    ownerDto.setPrefs(mapper.toDto(prefs));

    var notif = notifRepo.findById(p.getId()).orElseGet(() -> defaultsNotif(p));
    ownerDto.setNotificationSettings(mapper.toDto(notif));

    var stats = statsRepo.findById(p.getId()).orElseGet(() -> defaultsStats(p));
    ownerDto.setStats(mapper.toDto(stats));

    var listings = listingService.listMyActive(p.getUserId());
    ownerDto.setListings(listings);

    return ownerDto;
  }

  @Transactional
  public ProfileOwnerDto updateMyProfile(Long userId, ProfileUpdateRequest req) {
    var p = loadByUserId(userId);
    if (req == null) throw new ValidationException("request body is required");

    if (req.getUsername() != null) {
      var newU = req.getUsername().trim().toLowerCase();
      if (!newU.matches("^[a-z0-9_]{3,32}$"))
        throw new ValidationException("username must match ^[a-z0-9_]{3,32}$");
      if (ReservedUsernames.isReserved(newU))
        throw new BusinessRuleException("username is reserved");
      if (!newU.equalsIgnoreCase(p.getUsername())
          && profileRepo.existsByUsernameIgnoreCase(newU))
        throw new BusinessRuleException("username is already taken");
      req.setUsername(newU);
    }

    mapper.updateProfileFromDto(req, p);
    profileRepo.save(p);
    return getMyProfile(userId);
  }

  @Transactional
  public ProfileOwnerDto updateMyAvatar(Long userId, String avatarUrl) {
    var p = loadByUserId(userId);
    if (avatarUrl == null || avatarUrl.isBlank())
      throw new ValidationException("avatarUrl is required");
    p.setAvatarUrl(avatarUrl.trim());
    profileRepo.save(p);
    return getMyProfile(userId);
  }

  @Transactional
  public ProfileOwnerDto updateMyBanner(Long userId, String bannerUrl) {
    var p = loadByUserId(userId);
    if (bannerUrl == null || bannerUrl.isBlank())
      throw new ValidationException("bannerUrl is required");
    p.setBannerUrl(bannerUrl.trim());
    profileRepo.save(p);
    return getMyProfile(userId);
  }

  @Transactional
  public List<ProfileLinkDto> upsertMyLinks(Long userId, List<ProfileLinkDto> links) {
    var p = loadByUserId(userId);
    if (links == null) links = List.of();
    if (links.size() > 8) throw new BusinessRuleException("maximum 8 links allowed");

    var idxSet = new HashSet<Integer>();
    for (var l : links) {
      if (l.getIdx() == null || l.getIdx() < 0) throw new ValidationException("idx must be >= 0");
      if (!idxSet.add(l.getIdx())) throw new ValidationException("idx must be unique");
      if (l.getType() == null) throw new ValidationException("type is required");
      if (l.getUrl() == null || l.getUrl().isBlank()) throw new ValidationException("url is required");
    }

    linkRepo.deleteByProfile_Id(p.getId());

    var entities = links.stream()
        .sorted(Comparator.comparingInt(ProfileLinkDto::getIdx))
        .map(dto -> {
          LinkValidator.validate(dto.getType(), dto.getUrl());
          return ProfileLink.builder()
              .profile(p)
              .type(dto.getType())
              .label(dto.getLabel())
              .url(dto.getUrl().trim())
              .idx(dto.getIdx())
              .isPublic(dto.getIsPublic() == null ? Boolean.TRUE : dto.getIsPublic())
              .build();
        }).toList();

    var saved = linkRepo.saveAll(entities);
    return mapper.toLinkDtoList(saved);
  }

  @Transactional
  public ProfilePrefsDto updateMyPrefs(Long userId, ProfilePrefsUpdateRequest req) {
    var p = loadByUserId(userId);
    var prefs = prefsRepo.findById(p.getId()).orElseGet(() -> defaultsPrefs(p));
    mapper.updatePrefsFromDto(req, prefs);
    prefsRepo.save(prefs);
    return mapper.toDto(prefs);
  }

  @Transactional
  public NotificationSettingsDto updateMyNotificationSettings(Long userId, NotificationSettingsUpdateRequest req) {
    var p = loadByUserId(userId);
    var ns = notifRepo.findById(p.getId()).orElseGet(() -> defaultsNotif(p));
    mapper.updateNotifFromDto(req, ns);
    notifRepo.save(ns);
    return mapper.toDto(ns);
  }

  /* -------------------- helpers -------------------- */

  private String generateUniqueUsername(String base) {
    String seed = base == null ? "user" : base.trim().toLowerCase().replaceAll("[^a-z0-9_]", "_");
    if (seed.length() < 3) seed = seed + "123";
    if (seed.length() > 16) seed = seed.substring(0, 16);

    String u = seed;
    int i = 1;
    while (profileRepo.existsByUsernameIgnoreCase(u)
        || ReservedUsernames.isReserved(u)) {
      u = seed + (i++);
    }
    return u;
  }

  private Profile loadByUserId(Long userId) {
    if (userId == null) throw new ValidationException("userId is required");
    return profileRepo.findByUserId(userId)
        .orElseThrow(() -> new NotFoundException("Profile for user not found"));
  }

  private ProfilePrefs defaultsPrefs(Profile p) {
    var np = ProfilePrefs.builder().profile(p).build();
    return prefsRepo.save(np);
  }

  private NotificationSettings defaultsNotif(Profile p) {
    var ns = NotificationSettings.builder().profile(p).build();
    return notifRepo.save(ns);
  }

  private ProfileStats defaultsStats(Profile p) {
    var s = ProfileStats.builder().profile(p).build();
    return statsRepo.save(s);
  }
}
