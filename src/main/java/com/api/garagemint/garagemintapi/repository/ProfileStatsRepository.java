package com.api.garagemint.garagemintapi.repository;

import com.api.garagemint.garagemintapi.model.profile.ProfileStats;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProfileStatsRepository extends JpaRepository<ProfileStats, Long> {
    // PK = profile_id
}