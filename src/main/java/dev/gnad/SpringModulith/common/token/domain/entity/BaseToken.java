package dev.gnad.SpringModulith.common.token.domain.entity;

import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;


@MappedSuperclass
@Data
public class BaseToken {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

    @Column(name = "token_raw", nullable = false, unique = true)
    private String tokenRaw;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(nullable = false)
    private Instant expireAt;

    private Instant revokedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    public boolean isExpired(){
        return Instant.now().isAfter(expireAt);

    }
    public boolean isRevoked(){
        return revokedAt != null;
    }
}
