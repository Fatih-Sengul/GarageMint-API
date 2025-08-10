package com.api.garagemint.garagemintapi.mapper.profile;

import com.api.garagemint.garagemintapi.dto.profile.*;
import com.api.garagemint.garagemintapi.model.*;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

  // Entity -> DTO
  @Mapping(target = "links", ignore = true)
  @Mapping(target = "featuredItems", ignore = true)
  @Mapping(target = "stats", ignore = true)
  ProfilePublicDto toPublicDto(Profile profile);

  @Mapping(target = "links", ignore = true)
  @Mapping(target = "featuredItems", ignore = true)
  @Mapping(target = "prefs", ignore = true)
  @Mapping(target = "notificationSettings", ignore = true)
  @Mapping(target = "stats", ignore = true)
  ProfileOwnerDto toOwnerDto(Profile profile);

  // Links
  @Mapping(target = "type", expression = "java(link.getType().name())")
  ProfileLinkDto toDto(ProfileLink link);
  List<ProfileLinkDto> toLinkDtoList(List<ProfileLink> links);

  // Prefs / Notif / Stats
  ProfilePrefsDto toDto(ProfilePrefs prefs);
  NotificationSettingsDto toDto(NotificationSettings ns);
  ProfileStatsDto toDto(ProfileStats stats);

  // Featured
  @Mapping(target = "itemId", source = "id.itemId")
  ProfileFeaturedItemDto toDto(ProfileFeaturedItem fi);
  List<ProfileFeaturedItemDto> toFeaturedDtoList(List<ProfileFeaturedItem> list);

  // Update mappings (DTO -> Entity) — owner update
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateProfileFromDto(ProfileUpdateRequest req, @MappingTarget Profile entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updatePrefsFromDto(ProfilePrefsUpdateRequest req, @MappingTarget ProfilePrefs entity);

  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateNotifFromDto(NotificationSettingsUpdateRequest req, @MappingTarget NotificationSettings entity);
}
