package com.api.garagemint.garagemintapi.service.profile;

import com.api.garagemint.garagemintapi.dto.profile.*;
import com.api.garagemint.garagemintapi.mapper.profile.ProfileMapper;
import com.api.garagemint.garagemintapi.model.*;
import com.api.garagemint.garagemintapi.repository.NotificationSettingsRepository;
import com.api.garagemint.garagemintapi.repository.ProfileFeaturedItemRepository;
import com.api.garagemint.garagemintapi.repository.ProfileLinkRepository;
import com.api.garagemint.garagemintapi.repository.ProfilePrefsRepository;
import com.api.garagemint.garagemintapi.repository.ProfileRepository;
import com.api.garagemint.garagemintapi.repository.ProfileStatsRepository;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
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
  private final ProfileFeaturedItemRepository featuredRepo;
  private final ProfileMapper mapper;

  /* -------------------- PUBLIC -------------------- */

  @Transactional(readOnly = true)
  public ProfilePublicDto getPublicProfileByUsername(String username) {
    if (username == null || username.isBlank()) throw new ValidationException("username is required");
    var p = profileRepo.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new NotFoundException("Profile not found"));

    var dto = mapper.toPublicDto(p);

    if (!Boolean.TRUE.equals(p.isPublic())) {
      dto.setBio(null);
      dto.setBannerUrl(null);
      dto.setLocation(null);
      dto.setWebsiteUrl(null);
      dto.setLinks(List.of());
      dto.setFeaturedItems(List.of());
      dto.setStats(null);
      return dto;
    }

    var links = linkRepo.findByProfileIdAndIsPublicTrueOrderByIdxAsc(p.getId());
    dto.setLinks(mapper.toLinkDtoList(links));

    var feats = featuredRepo.findTop9ByIdProfileIdOrderByIdxAsc(p.getId());
    dto.setFeaturedItems(mapper.toFeaturedDtoList(feats));

    var stats = statsRepo.findById(p.getId())
        .orElseGet(() -> ProfileStats.builder().profileId(p.getId()).build());
    dto.setStats(mapper.toDto(stats));

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

          prefsRepo.save(ProfilePrefs.builder()
              .profileId(p.getId()).build());
          notifRepo.save(NotificationSettings.builder()
              .profileId(p.getId()).build());
          statsRepo.save(ProfileStats.builder()
              .profileId(p.getId()).build());

          return getMyProfile(userId);
        });
  }

  @Transactional(readOnly = true)
  public ProfileOwnerDto getMyProfile(Long userId) {
    var p = loadByUserId(userId);
    var ownerDto = mapper.toOwnerDto(p);

    var links = linkRepo.findByProfileIdOrderByIdxAsc(p.getId());
    ownerDto.setLinks(mapper.toLinkDtoList(links));

    var prefs = prefsRepo.findById(p.getId()).orElseGet(() -> defaultsPrefs(p.getId()));
    ownerDto.setPrefs(mapper.toDto(prefs));

    var notif = notifRepo.findById(p.getId()).orElseGet(() -> defaultsNotif(p.getId()));
    ownerDto.setNotificationSettings(mapper.toDto(notif));

    var stats = statsRepo.findById(p.getId()).orElseGet(() -> defaultsStats(p.getId()));
    ownerDto.setStats(mapper.toDto(stats));

    var feats = featuredRepo.findByIdProfileIdOrderByIdxAsc(p.getId());
    ownerDto.setFeaturedItems(mapper.toFeaturedDtoList(feats));

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

    linkRepo.deleteByProfileId(p.getId());

    var entities = links.stream()
        .sorted(Comparator.comparingInt(ProfileLinkDto::getIdx))
        .map(dto -> {
          var type = ProfileLinkType.valueOf(dto.getType().toUpperCase());
          LinkValidator.validate(type, dto.getUrl());
          return ProfileLink.builder()
              .profileId(p.getId())
              .type(type)
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
    var prefs = prefsRepo.findById(p.getId()).orElseGet(() -> defaultsPrefs(p.getId()));
    mapper.updatePrefsFromDto(req, prefs);
    prefsRepo.save(prefs);
    return mapper.toDto(prefs);
  }

  @Transactional
  public NotificationSettingsDto updateMyNotificationSettings(Long userId, NotificationSettingsUpdateRequest req) {
    var p = loadByUserId(userId);
    var ns = notifRepo.findById(p.getId()).orElseGet(() -> defaultsNotif(p.getId()));
    mapper.updateNotifFromDto(req, ns);
    notifRepo.save(ns);
    return mapper.toDto(ns);
  }

  @Transactional
  public List<ProfileFeaturedItemDto> updateMyFeaturedItems(Long userId, List<ProfileFeaturedItemDto> items) {
    var p = loadByUserId(userId);
    if (items == null) items = List.of();
    if (items.size() > 9) throw new BusinessRuleException("maximum 9 featured items allowed");

    var idxSet = new HashSet<Integer>();
    var idSet = new HashSet<Long>();
    for (var it : items) {
      if (it.getItemId() == null) throw new ValidationException("itemId is required");
      if (!idSet.add(it.getItemId())) throw new ValidationException("duplicate itemId");
      if (it.getIdx() == null || it.getIdx() < 0) throw new ValidationException("idx must be >= 0");
      if (!idxSet.add(it.getIdx())) throw new ValidationException("idx must be unique");
    }

    featuredRepo.deleteByIdProfileId(p.getId());

    var toSave = items.stream()
        .sorted(Comparator.comparingInt(ProfileFeaturedItemDto::getIdx))
        .map(it -> ProfileFeaturedItem.builder()
            .id(new FeaturedItemId(p.getId(), it.getItemId()))
            .idx(it.getIdx())
            .build())
        .toList();

    var saved = featuredRepo.saveAll(toSave);
    return mapper.toFeaturedDtoList(saved);
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

  private ProfilePrefs defaultsPrefs(Long profileId) {
    var p = ProfilePrefs.builder().profileId(profileId).build();
    return prefsRepo.save(p);
  }

  private NotificationSettings defaultsNotif(Long profileId) {
    var n = NotificationSettings.builder().profileId(profileId).build();
    return notifRepo.save(n);
  }

  private ProfileStats defaultsStats(Long profileId) {
    var s = ProfileStats.builder().profileId(profileId).build();
    return statsRepo.save(s);
  }
}
