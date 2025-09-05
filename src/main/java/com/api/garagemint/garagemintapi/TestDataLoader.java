package com.api.garagemint.garagemintapi;

import com.api.garagemint.garagemintapi.model.auth.UserAccount;
import com.api.garagemint.garagemintapi.model.auth.UserRole;
import com.api.garagemint.garagemintapi.repository.auth.UserAccountRepository;
import com.api.garagemint.garagemintapi.service.profile.ProfileService;
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
 */
@Profile({"dev", "test"})
@Component
@RequiredArgsConstructor
public class TestDataLoader implements CommandLineRunner {

  private final UserAccountRepository userAccountRepository;
  private final ProfileService profileService;
  private final PasswordEncoder passwordEncoder;

  @Override
  @Transactional
  public void run(String... args) {
    seedUsersWithProfiles();
  }

  /**
   * Create demo users and matching profiles when the database is empty.
   */
  private void seedUsersWithProfiles() {
    if (userAccountRepository.count() > 0) {
      return;
    }

    List<UserAccount> users = List.of(
        UserAccount.builder()
            .email("user1@mail.test")
            .username("user1")
            .password(passwordEncoder.encode("Passw0rd!"))
            .enabled(true)
            .role(UserRole.USER)
            .build(),
        UserAccount.builder()
            .email("user2@mail.test")
            .username("user2")
            .password(passwordEncoder.encode("Passw0rd!"))
            .enabled(true)
            .role(UserRole.USER)
            .build(),
        UserAccount.builder()
            .email("user3@mail.test")
            .username("user3")
            .password(passwordEncoder.encode("Passw0rd!"))
            .enabled(true)
            .role(UserRole.USER)
            .build()
    );

    users.forEach(u -> {
      UserAccount saved = userAccountRepository.save(u);
      profileService.ensureMyProfile(saved.getId());
    });
  }
}

