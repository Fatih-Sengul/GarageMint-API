package com.api.garagemint.garagemintapi.repository;


import com.api.garagemint.garagemintapi.model.NotificationSettings;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NotificationSettingsRepository extends JpaRepository<NotificationSettings, Long> {
    // PK = profile_id
}