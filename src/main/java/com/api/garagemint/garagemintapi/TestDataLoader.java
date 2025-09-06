package com.api.garagemint.garagemintapi;

import com.api.garagemint.garagemintapi.model.auth.UserAccount;
import com.api.garagemint.garagemintapi.model.auth.UserRole;
import com.api.garagemint.garagemintapi.model.auction.Auction;
import com.api.garagemint.garagemintapi.model.auction.AuctionBid;
import com.api.garagemint.garagemintapi.model.auction.AuctionImage;
import com.api.garagemint.garagemintapi.model.auction.AuctionStatus;
import com.api.garagemint.garagemintapi.model.cars.Condition;
import com.api.garagemint.garagemintapi.model.cars.Listing;
import com.api.garagemint.garagemintapi.model.cars.ListingImage;
import com.api.garagemint.garagemintapi.model.cars.ListingStatus;
import com.api.garagemint.garagemintapi.model.cars.ListingType;
import com.api.garagemint.garagemintapi.repository.auth.UserAccountRepository;
import com.api.garagemint.garagemintapi.repository.auction.AuctionBidRepository;
import com.api.garagemint.garagemintapi.repository.auction.AuctionImageRepository;
import com.api.garagemint.garagemintapi.repository.auction.AuctionRepository;
import com.api.garagemint.garagemintapi.repository.cars.ListingImageRepository;
import com.api.garagemint.garagemintapi.repository.cars.ListingRepository;
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
  private final AuctionRepository auctionRepository;
  private final AuctionImageRepository auctionImageRepository;
  private final AuctionBidRepository auctionBidRepository;
  private final PasswordEncoder passwordEncoder;

  private static final String DEMO_PASSWORD = "Password1!";

  private final List<UserAccount> demoUsers = new ArrayList<>();
  private final List<Listing> demoListings = new ArrayList<>();

  @Override
  @Transactional
  public void run(String... args) {
    seedUsersWithProfiles();
    seedFollows();
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

  private void seedListings() {
    if (demoUsers.size() < 3 || listingRepository.count() > 0) return;

    Listing sale = listingRepository.save(Listing.builder()
        .sellerUserId(demoUsers.get(0).getId())
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
    demoListings.add(sale);

    Listing trade = listingRepository.save(Listing.builder()
        .sellerUserId(demoUsers.get(1).getId())
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
    demoListings.add(trade);
  }

  private void seedAuctions() {
    if (demoUsers.size() < 3 || auctionRepository.count() > 0) return;

    Instant now = Instant.now();
    Long listingId = demoListings.isEmpty() ? null : demoListings.get(0).getId();

    Auction auction = auctionRepository.save(Auction.builder()
        .sellerUserId(demoUsers.get(2).getId())
        .listingId(listingId)
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
        .auctionId(auction.getId())
        .url("https://example.com/laferrari.jpg")
        .idx(0)
        .build());

    auctionBidRepository.save(AuctionBid.builder()
        .auction(auction)
        .bidderUserId(demoUsers.get(0).getId())
        .amount(new BigDecimal("110"))
        .build());
    AuctionBid bid2 = auctionBidRepository.save(AuctionBid.builder()
        .auction(auction)
        .bidderUserId(demoUsers.get(1).getId())
        .amount(new BigDecimal("120"))
        .build());

    auction.setHighestBidAmount(bid2.getAmount());
    auction.setHighestBidUserId(demoUsers.get(1).getId());
    auctionRepository.save(auction);
  }
}

