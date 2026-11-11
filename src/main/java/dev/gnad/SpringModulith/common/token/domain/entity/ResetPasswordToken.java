package dev.gnad.SpringModulith.common.token.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(
        name = "reset_password_token",
        indexes = {
                @Index(
                        name = "idx_reset_password_token",
                        columnList = "token_hash",
                        unique = true
                )
        }
)
public class ResetPasswordToken extends BaseToken{
}
