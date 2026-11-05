package dev.gnad.SpringModulith.module.Auth.internal.repository;

import dev.gnad.SpringModulith.module.Auth.domain.entity.RefreshToken;
import dev.gnad.SpringModulith.module.Auth.domain.entity.ResetPasswordToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Optional;

@Repository
public interface ResetPasswordTokenRepository extends JpaRepository<ResetPasswordToken,Long> {
    Optional<ResetPasswordToken> findByTokenHash(String tokenHash);
}
