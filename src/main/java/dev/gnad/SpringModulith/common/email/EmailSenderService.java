package dev.gnad.SpringModulith.common.email;


import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailSenderService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String senderEmail;

    public EmailSenderService(JavaMailSender javaMailSender) {
        this.javaMailSender = javaMailSender;
    }

    public void sendOtp(String recipientEmail, String otp){
        try{

            MimeMessage message = javaMailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message,true,"UTF-8");

            helper.setFrom(senderEmail);
            helper.setTo(recipientEmail);
            helper.setSubject("Password Reset OTP");

            String htmlContent = """
                <html>
                    <body>
                        <h2>Password Reset</h2>

                        <p>
                            You requested to reset your password.
                            Please use the OTP below:
                        </p>

                        <h1 style="color: #2563eb;
                                   letter-spacing: 8px;">
                            %s
                        </h1>

                        <p>
                            This OTP will expire in 5 minutes.
                        </p>

                        <p>
                            If you did not request this operation,
                            please ignore this email.
                        </p>
                    </body>
                </html>
                """.formatted(otp);
            helper.setText(htmlContent, true);

            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.printf("Failed to send OTP email to {}", recipientEmail, e);
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }
}
