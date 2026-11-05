package dev.gnad.SpringModulith.common.otp.internal.respository;


import dev.gnad.SpringModulith.common.otp.domain.entity.Otp;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface OtpRepository extends JpaRepository<Otp,Long> {
    Optional<Otp> findByOtpHashAndUser_email(String tokenHash, String email);
}
