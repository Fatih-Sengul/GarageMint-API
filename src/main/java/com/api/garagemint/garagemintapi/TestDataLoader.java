package com.api.garagemint.garagemintapi;

import com.api.garagemint.garagemintapi.model.auth.UserAccount;
import com.api.garagemint.garagemintapi.model.auth.UserRole;
import com.api.garagemint.garagemintapi.model.auction.Auction;
import com.api.garagemint.garagemintapi.model.auction.AuctionBid;
import com.api.garagemint.garagemintapi.model.auction.AuctionImage;
import com.api.garagemint.garagemintapi.model.auction.AuctionStatus;
import com.api.garagemint.garagemintapi.model.cars.*;
import com.api.garagemint.garagemintapi.repository.auth.UserAccountRepository;
import com.api.garagemint.garagemintapi.repository.auction.AuctionBidRepository;
import com.api.garagemint.garagemintapi.repository.auction.AuctionImageRepository;
import com.api.garagemint.garagemintapi.repository.auction.AuctionRepository;
import com.api.garagemint.garagemintapi.repository.cars.*;
import com.api.garagemint.garagemintapi.service.profile.ProfileFollowService;
import com.api.garagemint.garagemintapi.service.profile.ProfileService;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Loads a small set of data for development and integration tests.
 *
 * <p>The previous implementation attempted to populate almost every domain
 * entity and was tightly coupled to repository internals.  After the
 * introduction of Spring Security and PostgreSQL, the old approach started to
 * fail during application start-up.  This streamlined loader focuses only on
 * creating a couple of user accounts and the corresponding profiles.  It keeps
 * dependencies minimal which makes the start-up process more predictable and
 * easier to maintain.</p>
 *
 * <p>The seeded accounts can be used to exercise the authentication flow.
 * Use either the e-mail address or username together with the shared
 * password when invoking the <code>/auth/login</code> endpoint.  Both
 * fields are required by {@link com.api.garagemint.garagemintapi.dto.auth.LoginRequest}.
 * Example request body:
 * <pre>{@code {"emailOrUsername": "user1", "password": "Password1!"}}</pre>
 */
@Profile({"dev", "test"})
@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

  private final UserAccountRepository userAccountRepository;
  private final ProfileService profileService;
  private final ProfileFollowService profileFollowService;
  private final ListingRepository listingRepository;
  private final ListingImageRepository listingImageRepository;
  private final BrandRepository brandRepository;
  private final SeriesRepository seriesRepository;
  private final TagRepository tagRepository;
  private final ListingTagRepository listingTagRepository;
  private final AuctionRepository auctionRepository;
  private final AuctionImageRepository auctionImageRepository;
  private final AuctionBidRepository auctionBidRepository;
  private final PasswordEncoder passwordEncoder;

  private static final String DEMO_PASSWORD = "Password1!";

  private final List<UserAccount> demoUsers = new ArrayList<>();
  private final List<Listing> demoListings = new ArrayList<>();
  private final List<Brand> demoBrands = new ArrayList<>();
  private final List<Series> demoSeries = new ArrayList<>();
  private final List<Tag> demoTags = new ArrayList<>();

  @Override
  @Transactional
  public void run(String... args) {
    seedUsersWithProfiles();
    seedFollows();
    seedBrandsSeriesAndTags();
    seedListings();
    seedAuctions();
  }

  /**
   * Create demo users and matching profiles when the database is empty.
   */
  private void seedUsersWithProfiles() {
    if (userAccountRepository.count() == 0) {
      List<UserAccount> users = List.of(
          UserAccount.builder()
              .email("user1@example.com")
              .username("user1")
              .password(passwordEncoder.encode(DEMO_PASSWORD))
              .enabled(true)
              .role(UserRole.USER)
              .build(),
          UserAccount.builder()
              .email("user2@example.com")
              .username("user2")
              .password(passwordEncoder.encode(DEMO_PASSWORD))
              .enabled(true)
              .role(UserRole.USER)
              .build(),
          UserAccount.builder()
              .email("user3@example.com")
              .username("user3")
              .password(passwordEncoder.encode(DEMO_PASSWORD))
              .enabled(true)
              .role(UserRole.USER)
              .build()
      );

      users.forEach(u -> {
        UserAccount saved = userAccountRepository.save(u);
        profileService.ensureMyProfile(saved.getId());
      });
    }

    demoUsers.clear();
    demoUsers.addAll(userAccountRepository.findAll());
  }

  private void seedFollows() {
    if (demoUsers.size() < 3) return;
    profileFollowService.follow(demoUsers.get(0).getId(), "user2");
    profileFollowService.follow(demoUsers.get(0).getId(), "user3");
    profileFollowService.follow(demoUsers.get(1).getId(), "user1");
    profileFollowService.follow(demoUsers.get(2).getId(), "user1");
  }

  private void seedBrandsSeriesAndTags() {
    if (brandRepository.count() == 0) {
      demoBrands.addAll(brandRepository.saveAll(List.of(
          Brand.builder().name("Hot Wheels").slug("hot-wheels").country("USA").build(),
          Brand.builder().name("Matchbox").slug("matchbox").country("UK").build(),
          Brand.builder().name("Tomica").slug("tomica").country("Japan").build()
      )));
    } else {
      demoBrands.addAll(brandRepository.findAll());
    }

    if (seriesRepository.count() == 0 && demoBrands.size() >= 3) {
      demoSeries.addAll(seriesRepository.saveAll(List.of(
          Series.builder().brandId(demoBrands.get(0).getId()).name("Mainline").slug("mainline").build(),
          Series.builder().brandId(demoBrands.get(1).getId()).name("Collectors").slug("collectors").build(),
          Series.builder().brandId(demoBrands.get(2).getId()).name("Premium").slug("premium").build()
      )));
    } else {
      demoSeries.addAll(seriesRepository.findAll());
    }

    if (tagRepository.count() == 0) {
      demoTags.addAll(tagRepository.saveAll(List.of(
          Tag.builder().name("Sports").slug("sports").build(),
          Tag.builder().name("Classic").slug("classic").build(),
          Tag.builder().name("Limited").slug("limited").build()
      )));
    } else {
      demoTags.addAll(tagRepository.findAll());
    }
  }

  private void seedListings() {
    if (demoUsers.size() < 3 || demoBrands.size() < 3 || listingRepository.count() > 0) return;

    Listing sale = listingRepository.save(Listing.builder()
        .sellerUserId(demoUsers.get(0).getId())
        .brandId(demoBrands.get(0).getId())
        .seriesId(demoSeries.get(0).getId())
        .title("Hot Wheels Camaro")
        .description("Mint condition 2020 Camaro in red.")
        .modelName("Camaro")
        .scale("1:64")
        .modelYear((short) 2020)
        .condition(Condition.MINT)
        .type(ListingType.SALE)
        .price(new BigDecimal("50"))
        .currency("USD")
        .location("Istanbul")
        .status(ListingStatus.ACTIVE)
        .isActive(true)
        .build());
    listingImageRepository.save(ListingImage.builder()
        .listingId(sale.getId())
        .url("https://example.com/camaro.jpg")
        .idx(0)
        .build());
    listingTagRepository.save(ListingTag.builder()
        .id(new ListingTagId(sale.getId(), demoTags.get(0).getId()))
        .build());
    demoListings.add(sale);

    Listing trade = listingRepository.save(Listing.builder()
        .sellerUserId(demoUsers.get(1).getId())
        .brandId(demoBrands.get(1).getId())
        .seriesId(demoSeries.get(1).getId())
        .title("Trade Porsche 911 GT3")
        .description("Looking to trade for another 1:18 model.")
        .modelName("Porsche 911 GT3")
        .scale("1:18")
        .condition(Condition.USED)
        .type(ListingType.TRADE)
        .location("Ankara")
        .status(ListingStatus.ACTIVE)
        .isActive(true)
        .build());
    listingImageRepository.save(ListingImage.builder()
        .listingId(trade.getId())
        .url("https://example.com/gt3.jpg")
        .idx(0)
        .build());
    listingTagRepository.save(ListingTag.builder()
        .id(new ListingTagId(trade.getId(), demoTags.get(1).getId()))
        .build());
    demoListings.add(trade);

    Listing sale2 = listingRepository.save(Listing.builder()
        .sellerUserId(demoUsers.get(2).getId())
        .brandId(demoBrands.get(2).getId())
        .seriesId(demoSeries.get(2).getId())
        .title("Tomica Skyline")
        .description("JDM legend in 1:24 scale.")
        .modelName("Nissan Skyline")
        .scale("1:24")
        .modelYear((short) 1999)
        .condition(Condition.NEW)
        .type(ListingType.SALE)
        .price(new BigDecimal("70"))
        .currency("USD")
        .location("Bursa")
        .status(ListingStatus.ACTIVE)
        .isActive(true)
        .build());
    listingImageRepository.save(ListingImage.builder()
        .listingId(sale2.getId())
        .url("https://example.com/skyline.jpg")
        .idx(0)
        .build());
    listingTagRepository.save(ListingTag.builder()
        .id(new ListingTagId(sale2.getId(), demoTags.get(2).getId()))
        .build());
    demoListings.add(sale2);
  }

  private void seedAuctions() {
    if (demoUsers.size() < 3 || auctionRepository.count() > 0) return;

    Instant now = Instant.now();

    Auction auction1 = auctionRepository.save(Auction.builder()
        .sellerUserId(demoUsers.get(2).getId())
        .listingId(demoListings.get(0).getId())
        .title("Ferrari LaFerrari Auction")
        .description("Limited edition model.")
        .brand("Ferrari")
        .model("LaFerrari")
        .location("Izmir")
        .startPrice(new BigDecimal("100"))
        .startsAt(now.minusSeconds(3600))
        .endsAt(now.plusSeconds(86400))
        .status(AuctionStatus.ACTIVE)
        .build());
    auctionImageRepository.save(AuctionImage.builder()
        .auctionId(auction1.getId())
        .url("https://example.com/laferrari.jpg")
        .idx(0)
        .build());
    AuctionBid bid1 = auctionBidRepository.save(AuctionBid.builder()
        .auction(auction1)
        .bidderUserId(demoUsers.get(0).getId())
        .amount(new BigDecimal("110"))
        .build());
    AuctionBid bid2 = auctionBidRepository.save(AuctionBid.builder()
        .auction(auction1)
        .bidderUserId(demoUsers.get(1).getId())
        .amount(new BigDecimal("120"))
        .build());
    auction1.setHighestBidAmount(bid2.getAmount());
    auction1.setHighestBidUserId(demoUsers.get(1).getId());
    auctionRepository.save(auction1);

    Auction auction2 = auctionRepository.save(Auction.builder()
        .sellerUserId(demoUsers.get(0).getId())
        .listingId(demoListings.get(1).getId())
        .title("Porsche 911 Auction")
        .description("Rare GT3 model.")
        .brand("Porsche")
        .model("911 GT3")
        .location("Ankara")
        .startPrice(new BigDecimal("80"))
        .startsAt(now.minusSeconds(7200))
        .endsAt(now.plusSeconds(43200))
        .status(AuctionStatus.ACTIVE)
        .build());
    auctionImageRepository.save(AuctionImage.builder()
        .auctionId(auction2.getId())
        .url("https://example.com/911.jpg")
        .idx(0)
        .build());
    AuctionBid bid3 = auctionBidRepository.save(AuctionBid.builder()
        .auction(auction2)
        .bidderUserId(demoUsers.get(2).getId())
        .amount(new BigDecimal("90"))
        .build());
    auction2.setHighestBidAmount(bid3.getAmount());
    auction2.setHighestBidUserId(demoUsers.get(2).getId());
    auctionRepository.save(auction2);

    Auction auction3 = auctionRepository.save(Auction.builder()
        .sellerUserId(demoUsers.get(1).getId())
        .title("Mystery Lot Auction")
        .description("Box of assorted cars.")
        .brand("Various")
        .model("Lot")
        .location("Istanbul")
        .startPrice(new BigDecimal("60"))
        .startsAt(now.minusSeconds(1800))
        .endsAt(now.plusSeconds(86400))
        .status(AuctionStatus.ACTIVE)
        .build());
    auctionImageRepository.save(AuctionImage.builder()
        .auctionId(auction3.getId())
        .url("https://example.com/mystery.jpg")
        .idx(0)
        .build());
    AuctionBid bid4 = auctionBidRepository.save(AuctionBid.builder()
        .auction(auction3)
        .bidderUserId(demoUsers.get(0).getId())
        .amount(new BigDecimal("65"))
        .build());
    auction3.setHighestBidAmount(bid4.getAmount());
    auction3.setHighestBidUserId(demoUsers.get(0).getId());
    auctionRepository.save(auction3);
  }
}

