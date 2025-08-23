package com.api.garagemint.garagemintapi.repository;

import com.api.garagemint.garagemintapi.model.profile.Follow;
import com.api.garagemint.garagemintapi.model.profile.FollowId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FollowRepository extends JpaRepository<Follow, FollowId> {
  List<Follow> findByIdFollowerId(Long followerId);
  List<Follow> findByIdFolloweeId(Long followeeId);
  boolean existsByIdFollowerIdAndIdFolloweeId(Long followerId, Long followeeId);
  void deleteByIdFollowerIdAndIdFolloweeId(Long followerId, Long followeeId);
}
