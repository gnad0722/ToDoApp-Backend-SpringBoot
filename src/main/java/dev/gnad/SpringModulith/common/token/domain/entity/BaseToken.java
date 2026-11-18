package dev.gnad.SpringModulith.common.token.domain.entity;

import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;


@MappedSuperclass
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTokenRaw() {
        return tokenRaw;
    }

    public void setTokenRaw(String tokenRaw) {
        this.tokenRaw = tokenRaw;
    }

    public String getTokenHash() {
        return tokenHash;
    }

    public void setTokenHash(String tokenHash) {
        this.tokenHash = tokenHash;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Instant getRevokedAt() {
        return revokedAt;
    }

    public void setRevokedAt(Instant revokedAt) {
        this.revokedAt = revokedAt;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
