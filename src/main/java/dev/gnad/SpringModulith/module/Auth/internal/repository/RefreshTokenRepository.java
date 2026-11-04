package dev.gnad.SpringModulith.module.Auth.internal.repository;


import dev.gnad.SpringModulith.module.Auth.domain.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.sql.Ref;
import java.time.Instant;
import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken,Long> {
    Optional<RefreshToken> findByTokenHash(String tokenHash);

    @Modifying
    @Query("""
        UPDATE RefreshToken r
        SET r.revokedAt = :now
        WHERE r.user.id = :userId
          AND r.revokedAt IS NULL
    """)
    void revokeAllByUserId(
            @Param("userId") Long userId,
            @Param("now") Instant now
    );
}
