package dev.gnad.SpringModulith.common.token.domain.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.Data;

@Entity

//Database index is data structure which will help retrieving records
// more efficiently without to examine every row in table, but it also consumes storage and adds overhead when inserting or updating data.
@Table(
        name = "refresh_token",
        indexes = {
                @Index(
                        name = "idx_refresh_token_hash",
                        columnList = "token_hash",
                        unique = true
                )
        }
)
@Data
public class RefreshToken extends BaseToken{
}
