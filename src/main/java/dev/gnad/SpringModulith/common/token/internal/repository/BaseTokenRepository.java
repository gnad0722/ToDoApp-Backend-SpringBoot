package dev.gnad.SpringModulith.common.token.internal.repository;

import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository

public interface BaseTokenRepository extends JpaRepository<BaseToken, Long> {
    Optional<BaseToken> findByTokenHash(String tokenHash);
}
