package dev.gnad.SpringModulith.common.token.internal.repository;

import dev.gnad.SpringModulith.common.token.domain.entity.ResetPasswordToken;
import org.springframework.stereotype.Repository;

@Repository
public interface ResetPasswordTokenRepository extends BaseTokenRepository<ResetPasswordToken> {
}
