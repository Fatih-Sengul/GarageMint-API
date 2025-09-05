package com.api.garagemint.garagemintapi.repository.auth;

import com.api.garagemint.garagemintapi.model.auth.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
  Optional<UserAccount> findByEmailIgnoreCase(String email);
  Optional<UserAccount> findByUsernameIgnoreCase(String username);
  boolean existsByEmailIgnoreCase(String email);
  boolean existsByUsernameIgnoreCase(String username);
}
