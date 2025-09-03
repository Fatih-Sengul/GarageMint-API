// src/main/java/com/api/garagemint/garagemintapi/TestDataLoader.java
package com.api.garagemint.garagemintapi;

import com.api.garagemint.garagemintapi.model.profile.*;
import com.api.garagemint.garagemintapi.repository.profiles.*;

import com.api.garagemint.garagemintapi.model.cars.*;
import com.api.garagemint.garagemintapi.repository.cars.*;

import com.api.garagemint.garagemintapi.model.auction.*;
import com.api.garagemint.garagemintapi.repository.auction.*;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class TestDataLoader implements CommandLineRunner {

  // --- profile repos
  private final ProfileRepository profileRepository;
  private final ProfilePrefsRepository profilePrefsRepository;
  private final NotificationSettingsRepository notificationSettingsRepository;
  private final ProfileStatsRepository profileStatsRepository;
  private final ProfileLinkRepository profileLinkRepository;
  private final ProfileFollowRepository profileFollowRepository;

  // --- cars repos
  private final BrandRepository brandRepository;
  private final SeriesRepository seriesRepository;
  private final TagRepository tagRepository;
  private final ListingRepository listingRepository;
  private final ListingImageRepository listingImageRepository;
  private final ListingTagRepository listingTagRepository;

  // --- auction repos
  private final AuctionRepository auctionRepository;
  private final AuctionBidRepository auctionBidRepository;
  private final AuctionImageRepository auctionImageRepository;

  public TestDataLoader(ProfileRepository profileRepository,
                        ProfilePrefsRepository profilePrefsRepository,
                        NotificationSettingsRepository notificationSettingsRepository,
                        ProfileStatsRepository profileStatsRepository,
                        ProfileLinkRepository profileLinkRepository,
                        ProfileFollowRepository profileFollowRepository,
                        BrandRepository brandRepository,
                        SeriesRepository seriesRepository,
                        TagRepository tagRepository,
                        ListingRepository listingRepository,
                        ListingImageRepository listingImageRepository,
                        ListingTagRepository listingTagRepository,
                        AuctionRepository auctionRepository,
                        AuctionBidRepository auctionBidRepository,
                        AuctionImageRepository auctionImageRepository) {
    this.profileRepository = profileRepository;
    this.profilePrefsRepository = profilePrefsRepository;
    this.notificationSettingsRepository = notificationSettingsRepository;
    this.profileStatsRepository = profileStatsRepository;
    this.profileLinkRepository = profileLinkRepository;
    this.profileFollowRepository = profileFollowRepository;
    this.brandRepository = brandRepository;
    this.seriesRepository = seriesRepository;
    this.tagRepository = tagRepository;
    this.listingRepository = listingRepository;
    this.listingImageRepository = listingImageRepository;
    this.listingTagRepository = listingTagRepository;
    this.auctionRepository = auctionRepository;
    this.auctionBidRepository = auctionBidRepository;
    this.auctionImageRepository = auctionImageRepository;
  }

  @Override
  @Transactional
  public void run(String... args) {
    Map<Long, Profile> profiles = seedProfilesIfEmpty();      // userId -> Profile
    seedFollowsGraph(profiles);                                // follow ilişkileri + sayaçlar
    seedCarsIfEmpty();                                         // brand/series/tags/listings/images/tags
    seedAuctionsIfEmpty(profiles);                             // auctions/images/bids (çeşitli statüler)
  }

  /* ===================== PROFILES ===================== */

  /**
   * 5 demo profil oluşturur (user1..user5), prefs/notifications/stats/link ile.
   * Varsa dokunmaz, yoksa oluşturur. Profil map'ini döner (userId -> Profile).
   */
  private Map<Long, Profile> seedProfilesIfEmpty() {
    Map<Long, Profile> map = new HashMap<>();

    if (profileRepository.count() == 0) {
      for (long i = 1; i <= 5; i++) {
        Profile saved = profileRepository.save(Profile.builder()
                .userId(i)
                .username("user" + i)
                .displayName("User " + i)
                .bio("Bio for user " + i)
                .avatarUrl("https://i.pravatar.cc/150?img=" + (10 + i))
                .bannerUrl("https://picsum.photos/seed/banner" + i + "/1200/300")
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

        // Başlangıç sayaçları: follow grafikten sonra güncellenecek
        profileStatsRepository.save(ProfileStats.builder()
                .profile(ref)
                .listingsActiveCount(0)
                .listingsTotalCount(0)
                .followersCount(0)
                .followingCount(0)
                .responseRate((short) 95)
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

        map.put(i, ref);
      }
    } else {
      // Varsa mevcutları map'e doldur
      var all = profileRepository.findAll();
      for (Profile p : all) {
        map.put(p.getUserId(), p);
      }
    }
    return map;
  }

  /**
   * Takip grafiği oluşturur:
   * user1 -> user2, user3
   * user2 -> user1, user3
   * user3 -> user1
   * user4 -> user1, user2
   * user5 -> (kimseyi takip etmiyor)
   * Sayaçlar (followersCount/followingCount) atomik güncellenir.
   */
  private void seedFollowsGraph(Map<Long, Profile> profiles) {
    if (profiles.isEmpty()) return;

    // Eğer zaten ilişkiler varsa tekrar eklemeyelim (idempotent)
    if (profileFollowRepository.count() > 0) return;

    follow(profiles.get(1L), profiles.get(2L));
    follow(profiles.get(1L), profiles.get(3L));

    follow(profiles.get(2L), profiles.get(1L));
    follow(profiles.get(2L), profiles.get(3L));

    follow(profiles.get(3L), profiles.get(1L));

    follow(profiles.get(4L), profiles.get(1L));
    follow(profiles.get(4L), profiles.get(2L));
    // user5 kimseyi takip etmiyor

    // Stats’ları sağlaması açısından, son bir normalize geçişi
    normalizeFollowStats();
  }

  private void follow(Profile follower, Profile followed) {
    if (follower == null || followed == null || follower.getId().equals(followed.getId())) return;
    boolean exists = profileFollowRepository.existsByFollower_IdAndFollowed_Id(follower.getId(), followed.getId());
    if (exists) return;

    profileFollowRepository.save(ProfileFollow.builder()
            .follower(follower)
            .followed(followed)
            .build());

    // Sayaçlar
    var fStats = profileStatsRepository.findById(follower.getId()).orElse(null);
    if (fStats != null) {
      int cur = fStats.getFollowingCount() == null ? 0 : fStats.getFollowingCount();
      fStats.setFollowingCount(cur + 1);
      profileStatsRepository.save(fStats);
    }
    var dStats = profileStatsRepository.findById(followed.getId()).orElse(null);
    if (dStats != null) {
      int cur = dStats.getFollowersCount() == null ? 0 : dStats.getFollowersCount();
      dStats.setFollowersCount(cur + 1);
      profileStatsRepository.save(dStats);
    }
  }

  /**
   * Güvenlik için, DB’deki follow ilişkilerine göre tüm sayaçları yeniden hesaplar.
   */
  private void normalizeFollowStats() {
    var allProfiles = profileRepository.findAll();

    // Hazırlık: profId -> followersCount / followingCount
    Map<Long, Integer> followersMap = new HashMap<>();
    Map<Long, Integer> followingMap = new HashMap<>();
    for (Profile p : allProfiles) {
      followersMap.put(p.getId(), 0);
      followingMap.put(p.getId(), 0);
    }

    var allFollows = profileFollowRepository.findAll();
    for (ProfileFollow f : allFollows) {
      Long followerId = f.getFollower().getId();
      Long followedId = f.getFollowed().getId();
      followingMap.put(followerId, followingMap.getOrDefault(followerId, 0) + 1);
      followersMap.put(followedId, followersMap.getOrDefault(followedId, 0) + 1);
    }

    for (Profile p : allProfiles) {
      var stats = profileStatsRepository.findById(p.getId()).orElse(null);
      if (stats != null) {
        stats.setFollowersCount(followersMap.getOrDefault(p.getId(), 0));
        stats.setFollowingCount(followingMap.getOrDefault(p.getId(), 0));
        profileStatsRepository.save(stats);
      }
    }
  }

  /* ===================== CARS / LISTINGS ===================== */

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

      for (long i = 1; i <= 5; i++) {
        var l = Listing.builder()
                .sellerUserId((i % 5) + 1) // satıcı dağılımı: 2..5,1
                .title("Sample Listing " + i)
                .description("Demo description " + i)
                .brandId(brandId)
                .seriesId(seriesId)
                .modelName(i % 2 == 0 ? "Porsche 911 GT3" : "Nissan Skyline GT-R R34")
                .scale("1:64")
                .modelYear((short)(1995 + i))
                .condition(Condition.MINT)
                .limitedEdition(i % 3 == 0)
                .theme(i % 2 == 0 ? "Euro" : "JDM")
                .countryOfOrigin(i % 2 == 0 ? "Germany" : "Japan")
                .type(i % 4 == 0 ? ListingType.TRADE : ListingType.SALE)
                .price(i % 4 == 0 ? null : new BigDecimal("149.99").add(new BigDecimal(i * 10)))
                .currency(i % 4 == 0 ? null : "USD")
                .location("Izmir, TR")
                .status(ListingStatus.ACTIVE)
                .isActive(Boolean.TRUE)
                .build();

        l = listingRepository.save(l);

        listingImageRepository.saveAll(List.of(
                ListingImage.builder().listingId(l.getId()).url("https://picsum.photos/seed/list" + i + "/800/600").idx(0).build(),
                ListingImage.builder().listingId(l.getId()).url("https://picsum.photos/seed/list" + i + "b/800/600").idx(1).build()
        ));

        var allTags = tagRepository.findAll();
        var jdm = allTags.stream().filter(t -> "jdm".equalsIgnoreCase(t.getSlug())).findFirst().orElse(null);
        var movie = allTags.stream().filter(t -> "movie-car".equalsIgnoreCase(t.getSlug())).findFirst().orElse(null);
        var f1 = allTags.stream().filter(t -> "f1".equalsIgnoreCase(t.getSlug())).findFirst().orElse(null);

        if (jdm != null) listingTagRepository.save(new ListingTag(new ListingTagId(l.getId(), jdm.getId())));
        if (i % 2 == 0 && f1 != null) listingTagRepository.save(new ListingTag(new ListingTagId(l.getId(), f1.getId())));
        if (i % 3 == 0 && movie != null) listingTagRepository.save(new ListingTag(new ListingTagId(l.getId(), movie.getId())));
      }

      // Seller’ların stats’ına listing sayaçlarını yazalım
      var allListings = listingRepository.findAll();
      Map<Long, List<Listing>> bySeller = allListings.stream().collect(Collectors.groupingBy(Listing::getSellerUserId));
      for (Map.Entry<Long, List<Listing>> e : bySeller.entrySet()) {
        Long sellerUserId = e.getKey();
        List<Listing> ls = e.getValue();
        Profile seller = profileRepository.findByUserId(sellerUserId).orElse(null);
        if (seller == null) continue;
        var stats = profileStatsRepository.findById(seller.getId()).orElse(null);
        if (stats != null) {
          stats.setListingsTotalCount(ls.size());
          int active = (int) ls.stream().filter(x -> x.getStatus() == ListingStatus.ACTIVE && Boolean.TRUE.equals(x.getIsActive())).count();
          stats.setListingsActiveCount(active);
          profileStatsRepository.save(stats);
        }
      }
    }
  }

  /* ===================== AUCTIONS ===================== */

  private void seedAuctionsIfEmpty(Map<Long, Profile> profiles) {
    if (auctionRepository.count() > 0) return;

    var now = Instant.now();
    // Listing referansları — varsa ilk 3 ilanı kullan
    var listingIds = listingRepository.findAll().stream().map(Listing::getId).limit(3).toList();
    Long listing1 = listingIds.size() > 0 ? listingIds.get(0) : null;
    Long listing2 = listingIds.size() > 1 ? listingIds.get(1) : null;
    Long listing3 = listingIds.size() > 2 ? listingIds.get(2) : null;

    // 1) ACTIVE: devam eden, çok teklifli
    Auction a1 = Auction.builder()
            .sellerUserId(1L)
            .listingId(listing1)
            .startPrice(new BigDecimal("100.00"))
            .currency("TRY")
            .startsAt(now.minus(Duration.ofHours(2)))
            .endsAt(now.plus(Duration.ofDays(5)))
            .status(AuctionStatus.ACTIVE)
            .title("Hot Wheels R34 Skyline")
            .description("Mint condition, Japan domestic market livery.")
            .brand("Hot Wheels")
            .model("Nissan Skyline GT-R R34")
            .location("Izmir, TR")
            .build();
    a1 = auctionRepository.save(a1);
    seedAuctionImages(a1.getId(), "a1");
    seedBid(a1, 2L, "120.00");
    seedBid(a1, 3L, "135.00");
    seedBid(a1, 2L, "150.00");
    setHighest(a1.getId()); // highestBidAmount/userId güncelle

    // 2) SCHEDULED: 2 saat sonra başlayacak
    Auction a2 = Auction.builder()
            .sellerUserId(2L)
            .listingId(listing2)
            .startPrice(new BigDecimal("250.00"))
            .currency("TRY")
            .startsAt(now.plus(Duration.ofHours(2)))
            .endsAt(now.plus(Duration.ofDays(7)))
            .status(AuctionStatus.SCHEDULED)
            .title("Tarmac Works Porsche 911 GT3")
            .description("Numbered limited edition.")
            .brand("Tarmac Works")
            .model("Porsche 911 GT3")
            .location("Istanbul, TR")
            .build();
    a2 = auctionRepository.save(a2);
    seedAuctionImages(a2.getId(), "a2");

    // 3) ENDED: bitmiş, kazanan var
    Auction a3 = Auction.builder()
            .sellerUserId(3L)
            .listingId(listing3)
            .startPrice(new BigDecimal("80.00"))
            .currency("TRY")
            .startsAt(now.minus(Duration.ofDays(10)))
            .endsAt(now.minus(Duration.ofDays(2)))
            .status(AuctionStatus.ENDED)
            .title("F1 Collection McLaren")
            .description("Classic era, display only.")
            .brand("Hot Wheels")
            .model("McLaren F1")
            .location("Ankara, TR")
            .build();
    a3 = auctionRepository.save(a3);
    seedAuctionImages(a3.getId(), "a3");
    seedBid(a3, 4L, "95.00");
    seedBid(a3, 5L, "110.00");
    setHighest(a3.getId());

    // 4) CANCELLED: iptal, görsel yok
    Auction a4 = Auction.builder()
            .sellerUserId(4L)
            .listingId(null)
            .startPrice(new BigDecimal("60.00"))
            .currency("TRY")
            .startsAt(now.plus(Duration.ofHours(1)))
            .endsAt(now.plus(Duration.ofDays(5)))
            .status(AuctionStatus.CANCELLED)
            .title("Random Mixed Lot")
            .description("Bundle of mixed diecasts.")
            .brand(null)
            .model(null)
            .location("Bursa, TR")
            .build();
    auctionRepository.save(a4);
  }

  private void seedAuctionImages(Long auctionId, String seed) {
    // Repository metodu property adına uyuyor: deleteByAuctionId(...)
    auctionImageRepository.deleteByAuctionId(auctionId);

    auctionImageRepository.saveAll(List.of(
            AuctionImage.builder()
                    .auctionId(auctionId)
                    .url("https://picsum.photos/seed/" + seed + "c/900/600")
                    .idx(0)
                    .build(),
            AuctionImage.builder()
                    .auctionId(auctionId)
                    .url("https://picsum.photos/seed/" + seed + "d/900/600")
                    .idx(1)
                    .build(),
            AuctionImage.builder()
                    .auctionId(auctionId)
                    .url("https://picsum.photos/seed/" + seed + "e/900/600")
                    .idx(2)
                    .build()
    ));
  }


  private void seedBid(Auction a, Long bidderUserId, String amountStr) {
    auctionBidRepository.save(AuctionBid.builder()
            .auction(a)
            .bidderUserId(bidderUserId)
            .amount(new BigDecimal(amountStr))
            .build());
  }

  private void setHighest(Long auctionId) {
    var bids = auctionBidRepository.findByAuction_IdOrderByCreatedAtAsc(auctionId);
    if (bids.isEmpty()) return;
    var last = bids.get(bids.size() - 1);
    var a = auctionRepository.findById(auctionId).orElseThrow();
    a.setHighestBidAmount(last.getAmount());
    a.setHighestBidUserId(last.getBidderUserId());
    auctionRepository.save(a);
  }
}
