package dev.gnad.SpringModulith.common.otp.domain.entity;

import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Table(
        name = "otp_verifications",
        indexes = {
                @Index(
                        name = "idx_otp_verifications",
                        columnList = "token_hash",
                        unique = true
                )
        }
)
@Data
public class Otp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String otpHash;


    @Column(nullable = false)
    private Instant expiresAt = Instant.now().plusMillis(900000);

    private Instant verifiedAt;

    public boolean isExpired(){
        return Instant.now().isAfter(expiresAt);
    }

    public boolean isVerified(){
        return verifiedAt != null;
    }


}