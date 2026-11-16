package dev.gnad.SpringModulith.common.token.internal.repository;

import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@NoRepositoryBean
public interface BaseTokenRepository<T extends BaseToken>
        extends JpaRepository<T, Long> {

    Optional<T> findByTokenHash(String tokenHash);
}
