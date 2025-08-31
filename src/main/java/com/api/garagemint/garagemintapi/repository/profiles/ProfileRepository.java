package com.api.garagemint.garagemintapi.repository.profiles;


import com.api.garagemint.garagemintapi.model.profile.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

    Optional<Profile> findByUserId(Long userId);

    Optional<Profile> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);
}