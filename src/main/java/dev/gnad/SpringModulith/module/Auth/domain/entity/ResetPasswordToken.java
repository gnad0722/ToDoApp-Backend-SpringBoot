package dev.gnad.SpringModulith.module.Auth.domain.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

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
public class ResetPasswordToken {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String tokenHash;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant usedAt;

    private Instant createdAt = Instant.now();


    public boolean isExpired(){
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isUsed(){
        return usedAt != null;
    }
}
