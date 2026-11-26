package dev.gnad.SpringModulith.common.fcm.internal.repository;

import dev.gnad.SpringModulith.common.fcm.domain.entity.DeviceToken;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DeviceTokenRepository extends JpaRepository<DeviceToken, Integer> {
    List<DeviceToken> findByUserId(Integer userId);

    boolean existsByToken(String token);

    Optional<DeviceToken> findByToken(String token);

    @Transactional
    void deleteByToken(String token);
}
