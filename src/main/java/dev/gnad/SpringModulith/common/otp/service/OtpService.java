package dev.gnad.SpringModulith.common.otp.service;

import dev.gnad.SpringModulith.common.api.exception.BadRequestException;
import dev.gnad.SpringModulith.common.otp.domain.entity.Otp;
import dev.gnad.SpringModulith.common.otp.internal.respository.OtpRepository;
import dev.gnad.SpringModulith.module.Auth.domain.entity.User;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.HexFormat;

@Service
public class OtpService {

    private static final String INVALID_OTP = "Invalid or expired verification code";

    private final OtpRepository otpRepository;

    private final SecureRandom secureRandom = new SecureRandom();


    public OtpService(OtpRepository otpRepository) {
        this.otpRepository = otpRepository;
    }

    private String generateOtp() {
        int otp = secureRandom.nextInt(1_000_000);

        return String.format("%06d", otp);
    }

    private String hashOtp(String rawOtp){
        try {
            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash = digest.digest(
                    rawOtp.getBytes(StandardCharsets.UTF_8)
            );

            return HexFormat.of().formatHex(hash);

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }

    public String createOtp(User user){
        String rawOtp = this.generateOtp();

        String hashOtp = this.hashOtp(rawOtp);

        Otp otp = new Otp();

        otp.setOtpHash(hashOtp);
        otp.setUser(user);

        this.otpRepository.save(otp);

        return rawOtp;
    }

    public Otp validateOtp(String rawOtp, String email){
        String hashOtp = this.hashOtp(rawOtp);

        Otp otp = this.otpRepository.findByOtpHashAndUser_email(hashOtp, email)
                .orElseThrow(
                        ()-> new BadRequestException(INVALID_OTP)
                );

        if (otp.isExpired()){
            throw new BadRequestException(INVALID_OTP);
        }

        if (otp.isVerified()){
            throw new BadRequestException(INVALID_OTP);
        }
        return otp;
    }


    @Transactional
    public void verify(Otp otp){
        otp.setVerifiedAt(Instant.now());
        this.otpRepository.save(otp);
    }
}
