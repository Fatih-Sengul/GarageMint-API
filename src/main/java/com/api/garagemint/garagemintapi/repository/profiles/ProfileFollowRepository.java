package com.api.garagemint.garagemintapi.repository.profiles;

import com.api.garagemint.garagemintapi.model.profile.ProfileFollow;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileFollowRepository extends JpaRepository<ProfileFollow, Long> {

  boolean existsByFollower_IdAndFollowed_Id(Long followerProfileId, Long followedProfileId);

  long countByFollowed_Id(Long followedProfileId); // followers
  long countByFollower_Id(Long followerProfileId); // following

  Page<ProfileFollow> findByFollowed_Id(Long followedProfileId, Pageable pageable); // followers list
  Page<ProfileFollow> findByFollower_Id(Long followerProfileId, Pageable pageable); // following list

  void deleteByFollower_IdAndFollowed_Id(Long followerProfileId, Long followedProfileId);
}

