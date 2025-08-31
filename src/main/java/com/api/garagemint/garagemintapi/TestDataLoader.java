package com.api.garagemint.garagemintapi;

import com.api.garagemint.garagemintapi.model.profile.*;
import com.api.garagemint.garagemintapi.model.cars.*;
import com.api.garagemint.garagemintapi.repository.cars.*;
import com.api.garagemint.garagemintapi.repository.profiles.*;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

@Component
public class TestDataLoader implements CommandLineRunner {

  private final ProfileRepository profileRepository;
  private final ProfilePrefsRepository profilePrefsRepository;
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final ProfileStatsRepository profileStatsRepository;
  private final ProfileLinkRepository profileLinkRepository;

  // cars repos (mevcut)
  private final BrandRepository brandRepository;
  private final SeriesRepository seriesRepository;
  private final TagRepository tagRepository;
  private final ListingRepository listingRepository;
  private final ListingImageRepository listingImageRepository;
  private final ListingTagRepository listingTagRepository;

  public TestDataLoader(ProfileRepository profileRepository,
                        ProfilePrefsRepository profilePrefsRepository,
                        NotificationSettingsRepository notificationSettingsRepository,
                        ProfileStatsRepository profileStatsRepository,
                        ProfileLinkRepository profileLinkRepository,
                        BrandRepository brandRepository,
                        SeriesRepository seriesRepository,
                        TagRepository tagRepository,
                        ListingRepository listingRepository,
                        ListingImageRepository listingImageRepository,
                        ListingTagRepository listingTagRepository) {
    this.profileRepository = profileRepository;
    this.profilePrefsRepository = profilePrefsRepository;
    this.notificationSettingsRepository = notificationSettingsRepository;
    this.profileStatsRepository = profileStatsRepository;
    this.profileLinkRepository = profileLinkRepository;
    this.brandRepository = brandRepository;
    this.seriesRepository = seriesRepository;
    this.tagRepository = tagRepository;
    this.listingRepository = listingRepository;
    this.listingImageRepository = listingImageRepository;
    this.listingTagRepository = listingTagRepository;
  }

  @Override
  @Transactional
  public void run(String... args) {
    seedProfilesIfEmpty();
    seedCarsIfEmpty();
  }

  private void seedProfilesIfEmpty() {
    if (profileRepository.count() > 0) return;

    for (long i = 1; i <= 5; i++) {
      Profile saved = profileRepository.save(Profile.builder()
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

      Profile ref = profileRepository.getReferenceById(saved.getId());

      profilePrefsRepository.save(ProfilePrefs.builder()
          .profile(ref)
          .showEmail(i % 2 == 0)
          .showLocation(true)
          .showLinks(true)
          .searchable(true)
          .allowDm(true)
          .showCollection(false)
          .showListings(true)
          .build());

      notificationSettingsRepository.save(NotificationSettings.builder()
          .profile(ref)
          .emailGeneral(true)
          .emailMessage(true)
          .emailFavorite(true)
          .emailListingActivity(true)
          .pushGeneral(true)
          .digestFrequency(DigestFrequency.WEEKLY)
          .build());

      profileStatsRepository.save(ProfileStats.builder()
          .profile(ref)
          .listingsActiveCount(2)
          .listingsTotalCount(5)
          .followersCount(3)
          .followingCount(1)
          .responseRate((short) 90)
          .lastActiveAt(Instant.now())
          .build());

      profileLinkRepository.save(ProfileLink.builder()
          .profile(ref)
          .type(ProfileLinkType.INSTAGRAM)
          .label("IG " + i)
          .url("https://instagram.com/user" + i)
          .idx(0)
          .isPublic(true)
          .build());
    }
  }

  private void seedCarsIfEmpty() {
    if (brandRepository.count() == 0) {
      var hw = brandRepository.save(Brand.builder().name("Hot Wheels").slug("hot-wheels").country("USA").build());
      var tw = brandRepository.save(Brand.builder().name("Tarmac Works").slug("tarmac-works").country("Hong Kong").build());
      seriesRepository.save(Series.builder().brandId(hw.getId()).name("Fast & Furious").slug("fast-and-furious").build());
      seriesRepository.save(Series.builder().brandId(tw.getId()).name("Le Mans").slug("le-mans").build());
    }
    if (tagRepository.count() == 0) {
      tagRepository.save(Tag.builder().name("JDM").slug("jdm").build());
      tagRepository.save(Tag.builder().name("Movie Car").slug("movie-car").build());
      tagRepository.save(Tag.builder().name("F1").slug("f1").build());
    }
    if (listingRepository.count() == 0) {
      var brands = brandRepository.findAll();
      var series = seriesRepository.findAll();
      Long brandId = brands.isEmpty() ? null : brands.get(0).getId();
      Long seriesId = series.isEmpty() ? null : series.get(0).getId();

      for (long i = 1; i <= 3; i++) {
        var l = Listing.builder()
            .sellerUserId(i)
            .title("Sample Listing " + i)
            .description("Demo description " + i)
            .brandId(brandId)
            .seriesId(seriesId)
            .modelName("Nissan Skyline GT-R R34")
            .scale("1:64")
            .modelYear((short)(1998 + i))
            .condition(Condition.MINT)
            .limitedEdition(i != 2)
            .theme("JDM")
            .countryOfOrigin("Japan")
            .type(i == 3 ? ListingType.TRADE : ListingType.SALE)
            .price(i == 3 ? null : new BigDecimal("199.99"))
            .currency(i == 3 ? null : "USD")
            .location("Izmir, TR")
            .status(ListingStatus.ACTIVE)
            .isActive(Boolean.TRUE)
            .build();

        l = listingRepository.save(l);

        listingImageRepository.saveAll(List.of(
            ListingImage.builder().listingId(l.getId()).url("https://picsum.photos/seed/" + i + "/800/600").idx(0).build(),
            ListingImage.builder().listingId(l.getId()).url("https://picsum.photos/seed/" + i + "b/800/600").idx(1).build()
        ));

        var allTags = tagRepository.findAll();
        var jdm = allTags.stream().filter(t -> "jdm".equalsIgnoreCase(t.getSlug())).findFirst().orElse(null);
        var movie = allTags.stream().filter(t -> "movie-car".equalsIgnoreCase(t.getSlug())).findFirst().orElse(null);
        var f1 = allTags.stream().filter(t -> "f1".equalsIgnoreCase(t.getSlug())).findFirst().orElse(null);

        if (jdm != null) listingTagRepository.save(new ListingTag(new ListingTagId(l.getId(), jdm.getId())));
        if (i == 1 && movie != null) listingTagRepository.save(new ListingTag(new ListingTagId(l.getId(), movie.getId())));
        if (i == 2 && f1 != null) listingTagRepository.save(new ListingTag(new ListingTagId(l.getId(), f1.getId())));
      }
    }
  }
}
