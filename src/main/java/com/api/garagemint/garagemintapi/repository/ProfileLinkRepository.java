package com.api.garagemint.garagemintapi.repository;

import com.api.garagemint.garagemintapi.model.profile.ProfileLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileLinkRepository extends JpaRepository<ProfileLink, Long> {

    List<ProfileLink> findByProfileIdOrderByIdxAsc(Long profileId);

    List<ProfileLink> findByProfileIdAndIsPublicTrueOrderByIdxAsc(Long profileId);

    void deleteByProfileId(Long profileId);
}
