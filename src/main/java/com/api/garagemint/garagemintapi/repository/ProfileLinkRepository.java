package com.api.garagemint.garagemintapi.repository;

import com.api.garagemint.garagemintapi.model.profile.ProfileLink;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileLinkRepository extends JpaRepository<ProfileLink, Long> {

    List<ProfileLink> findByProfile_IdOrderByIdxAsc(Long profileId);

    List<ProfileLink> findByProfile_IdAndIsPublicTrueOrderByIdxAsc(Long profileId);

    void deleteByProfile_Id(Long profileId);

    /* Projection kullanım istersen:
       List<com.api.garagemint.garagemintapi.repository.projection.PublicLinkView> findByProfile_IdAndIsPublicTrueOrderByIdxAsc(Long profileId);
    */
}
