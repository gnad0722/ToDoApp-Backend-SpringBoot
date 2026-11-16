package dev.gnad.SpringModulith.common.token.internal.repository;

import dev.gnad.SpringModulith.common.token.domain.entity.BaseToken;
import dev.gnad.SpringModulith.common.token.domain.entity.RefreshToken;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RefreshTokenRepository extends BaseTokenRepository<RefreshToken>{
}
