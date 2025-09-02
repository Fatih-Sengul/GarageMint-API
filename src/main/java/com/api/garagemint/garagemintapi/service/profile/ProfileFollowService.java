package com.api.garagemint.garagemintapi.service.profile;

import com.api.garagemint.garagemintapi.dto.profile.*;
import com.api.garagemint.garagemintapi.model.profile.Profile;
import com.api.garagemint.garagemintapi.model.profile.ProfileFollow;
import com.api.garagemint.garagemintapi.repository.profiles.ProfileFollowRepository;
import com.api.garagemint.garagemintapi.repository.profiles.ProfileRepository;
import com.api.garagemint.garagemintapi.repository.profiles.ProfileStatsRepository;
import com.api.garagemint.garagemintapi.service.exception.BusinessRuleException;
import com.api.garagemint.garagemintapi.service.exception.NotFoundException;
import com.api.garagemint.garagemintapi.service.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProfileFollowService {

  private final ProfileRepository profileRepo;
  private final ProfileFollowRepository followRepo;
  private final ProfileStatsRepository statsRepo;

  private Profile loadByUsername(String username) {
    if (username == null || username.isBlank()) throw new ValidationException("username is required");
    return profileRepo.findByUsernameIgnoreCase(username)
        .orElseThrow(() -> new NotFoundException("Profile not found"));
  }

  @Transactional
  public void follow(Long meUserId, String targetUsername) {
    var me = profileRepo.findByUserId(meUserId)
        .orElseThrow(() -> new NotFoundException("My profile not found"));
    var target = loadByUsername(targetUsername);

    if (me.getId().equals(target.getId())) throw new BusinessRuleException("Cannot follow yourself");
    if (followRepo.existsByFollower_IdAndFollowed_Id(me.getId(), target.getId())) return; // idempotent

    followRepo.save(ProfileFollow.builder().follower(me).followed(target).build());

    // sayaçlar — atomik tutmak için basit +1/-1; istekte aynı tx içinde
    var targetStats = statsRepo.findById(target.getId()).orElse(null);
    if (targetStats != null) {
      targetStats.setFollowersCount((targetStats.getFollowersCount() == null ? 0 : targetStats.getFollowersCount()) + 1);
      statsRepo.save(targetStats);
    }
    var meStats = statsRepo.findById(me.getId()).orElse(null);
    if (meStats != null) {
      Integer old = meStats.getFollowingCount() == null ? 0 : meStats.getFollowingCount();
      meStats.setFollowingCount(old + 1);
      statsRepo.save(meStats);
    }
  }

  @Transactional
  public void unfollow(Long meUserId, String targetUsername) {
    var me = profileRepo.findByUserId(meUserId)
        .orElseThrow(() -> new NotFoundException("My profile not found"));
    var target = loadByUsername(targetUsername);

    if (!followRepo.existsByFollower_IdAndFollowed_Id(me.getId(), target.getId())) return; // idempotent
    followRepo.deleteByFollower_IdAndFollowed_Id(me.getId(), target.getId());

    var targetStats = statsRepo.findById(target.getId()).orElse(null);
    if (targetStats != null) {
      int cur = targetStats.getFollowersCount() == null ? 0 : targetStats.getFollowersCount();
      targetStats.setFollowersCount(Math.max(0, cur - 1));
      statsRepo.save(targetStats);
    }
    var meStats = statsRepo.findById(me.getId()).orElse(null);
    if (meStats != null) {
      int cur = meStats.getFollowingCount() == null ? 0 : meStats.getFollowingCount();
      meStats.setFollowingCount(Math.max(0, cur - 1));
      statsRepo.save(meStats);
    }
  }

  @Transactional(readOnly = true)
  public FollowListResponse listFollowers(String username, int page, int size) {
    var target = loadByUsername(username);
    var p = PageRequest.of(Math.max(0,page), Math.min(100, Math.max(1,size)), Sort.by(Sort.Direction.DESC, "createdAt"));
    var pageRes = followRepo.findByFollowed_Id(target.getId(), p);

    List<FollowUserDto> items = pageRes.getContent().stream().map(f -> {
      var u = f.getFollower();
      return FollowUserDto.builder()
          .id(u.getId())
          .username(u.getUsername())
          .displayName(u.getDisplayName())
          .avatarUrl(u.getAvatarUrl())
          .isVerified(u.isVerified())
          .build();
    }).toList();

    return FollowListResponse.builder()
        .items(items)
        .page(pageRes.getNumber())
        .size(pageRes.getSize())
        .totalElements(pageRes.getTotalElements())
        .totalPages(pageRes.getTotalPages())
        .build();
  }

  @Transactional(readOnly = true)
  public FollowListResponse listFollowing(String username, int page, int size) {
    var me = loadByUsername(username);
    var p = PageRequest.of(Math.max(0,page), Math.min(100, Math.max(1,size)), Sort.by(Sort.Direction.DESC, "createdAt"));
    var pageRes = followRepo.findByFollower_Id(me.getId(), p);

    List<FollowUserDto> items = pageRes.getContent().stream().map(f -> {
      var u = f.getFollowed();
      return FollowUserDto.builder()
          .id(u.getId())
          .username(u.getUsername())
          .displayName(u.getDisplayName())
          .avatarUrl(u.getAvatarUrl())
          .isVerified(u.isVerified())
          .build();
    }).toList();

    return FollowListResponse.builder()
        .items(items)
        .page(pageRes.getNumber())
        .size(pageRes.getSize())
        .totalElements(pageRes.getTotalElements())
        .totalPages(pageRes.getTotalPages())
        .build();
  }

  @Transactional(readOnly = true)
  public boolean isFollowing(Long viewerUserId, Long targetProfileId) {
    if (viewerUserId == null) return false;
    var viewerProfile = profileRepo.findByUserId(viewerUserId).orElse(null);
    if (viewerProfile == null) return false;
    return followRepo.existsByFollower_IdAndFollowed_Id(viewerProfile.getId(), targetProfileId);
  }
}

