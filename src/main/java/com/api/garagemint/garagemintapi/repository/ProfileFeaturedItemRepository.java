package com.api.garagemint.garagemintapi.repository;

import com.api.garagemint.garagemintapi.model.profile.ProfileFeaturedItem;
import com.api.garagemint.garagemintapi.model.profile.FeaturedItemId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProfileFeaturedItemRepository extends JpaRepository<ProfileFeaturedItem, FeaturedItemId> {

    List<ProfileFeaturedItem> findTop9ByIdProfileIdOrderByIdxAsc(Long profileId);

    List<ProfileFeaturedItem> findByIdProfileIdOrderByIdxAsc(Long profileId);

    void deleteByIdProfileId(Long profileId);

    void deleteByIdProfileIdAndIdItemId(Long profileId, Long itemId);
}