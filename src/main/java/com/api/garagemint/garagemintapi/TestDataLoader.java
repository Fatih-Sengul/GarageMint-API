package com.api.garagemint.garagemintapi;

import com.api.garagemint.garagemintapi.model.profile.*;
import com.api.garagemint.garagemintapi.repository.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.Instant;

@Component
public class TestDataLoader implements CommandLineRunner {

    private final ProfileRepository profileRepository;
    private final ProfilePrefsRepository profilePrefsRepository;
    private final NotificationSettingsRepository notificationSettingsRepository;
    private final ProfileStatsRepository profileStatsRepository;
    private final ProfileLinkRepository profileLinkRepository;
    private final ProfileFeaturedItemRepository profileFeaturedItemRepository;

    public TestDataLoader(ProfileRepository profileRepository,
                          ProfilePrefsRepository profilePrefsRepository,
                          NotificationSettingsRepository notificationSettingsRepository,
                          ProfileStatsRepository profileStatsRepository,
                          ProfileLinkRepository profileLinkRepository,
                          ProfileFeaturedItemRepository profileFeaturedItemRepository) {
        this.profileRepository = profileRepository;
        this.profilePrefsRepository = profilePrefsRepository;
        this.notificationSettingsRepository = notificationSettingsRepository;
        this.profileStatsRepository = profileStatsRepository;
        this.profileLinkRepository = profileLinkRepository;
        this.profileFeaturedItemRepository = profileFeaturedItemRepository;
    }

    @Override
    public void run(String... args) {
        if (profileRepository.count() > 0) {
            return;
        }

        for (long i = 1; i <= 5; i++) {
            Profile profile = profileRepository.save(Profile.builder()
                    .userId(i)
                    .username("user" + i)
                    .displayName("User " + i)
                    .bio("Bio for user " + i)
                    .avatarUrl("https://example.com/avatar" + i + ".png")
                    .bannerUrl("https://example.com/banner" + i + ".png")
                    .location("City " + i)
                    .websiteUrl("https://example.com/user" + i)
                    .language("en")
                    .isVerified(i % 2 == 0)
                    .isPublic(true)
                    .build());

            profilePrefsRepository.save(ProfilePrefs.builder()
                    .profileId(profile.getId())
                    .showEmail(i % 2 == 0)
                    .showLocation(true)
                    .showLinks(true)
                    .searchable(true)
                    .allowDm(true)
                    .showCollection(true)
                    .showListings(true)
                    .build());

            notificationSettingsRepository.save(NotificationSettings.builder()
                    .profileId(profile.getId())
                    .emailGeneral(true)
                    .emailMessage(true)
                    .emailFavorite(true)
                    .emailListingActivity(true)
                    .pushGeneral(true)
                    .digestFrequency("WEEKLY")
                    .build());

            profileStatsRepository.save(ProfileStats.builder()
                    .profileId(profile.getId())
                    .itemsCount(5)
                    .listingsActiveCount(2)
                    .favoritesCount(10)
                    .followersCount(3)
                    .responseRate((short) 90)
                    .lastActiveAt(Instant.now())
                    .build());

            profileLinkRepository.save(ProfileLink.builder()
                    .profileId(profile.getId())
                    .type(ProfileLinkType.INSTAGRAM)
                    .label("IG " + i)
                    .url("https://instagram.com/user" + i)
                    .idx(0)
                    .isPublic(true)
                    .build());

            profileFeaturedItemRepository.save(ProfileFeaturedItem.builder()
                    .id(new FeaturedItemId(profile.getId(), i))
                    .idx(0)
                    .build());
        }
    }
}
