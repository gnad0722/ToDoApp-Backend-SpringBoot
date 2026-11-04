package dev.gnad.SpringModulith.module.Auth.domain.entity;


import jakarta.persistence.*;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.time.Instant;

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
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "token_hash", nullable = false, unique = true)
    private String tokenHash;

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
