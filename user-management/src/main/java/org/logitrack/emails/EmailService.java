package org.logitrack.emails;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.logitrack.entities.User;
import org.logitrack.entities.VerificationToken;
import org.logitrack.repository.TokenRepository;
import org.logitrack.repository.UserRepository;
import org.springframework.mail.SimpleMailMessage;

import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {
    private final JavaMailSender javaMailSender;
    private final TokenRepository tokenRepository;
    private final UserRepository userRepository;

    @Async
    public void sendEmail(SimpleMailMessage email) {
        javaMailSender.send(email);
    }

    public String confirmEmail(String confirmationToken) {
        log.info("Trying to verify email");

        Optional<VerificationToken> veritoken = tokenRepository.findByConfirmationToken(confirmationToken);
        log.info("Verification token found in the database");

        if (veritoken.isPresent()) {
            VerificationToken newToken = veritoken.get();

            log.info("verification token retrieved from database" + newToken);
            // Check if the token has expired
            if (newToken.getExpirationTime().isBefore(LocalDateTime.now())) {
                log.info("token has expired");
                return "The confirmation token has expired.";
            }

            Optional<User> tokuser = userRepository.findByEmail(newToken.getUser().getEmail());
            log.info("User associated with token is present");

            if (tokuser.isPresent()) {
                User realUser = tokuser.get();
                log.info("User retrieved");
                realUser.setIsVerified(true);
                log.info("User is verified");
                userRepository.save(realUser);
                return "Account verified successfully.";
            } else {
                return "User not found for the provided confirmation token.";
            }
        } else {
            return "The provided confirmation token is invalid or expired!";
        }
    }


    public void sendConfirmationEmail(User user, String confirmationLink) {

        String subject = "Email Verification";

        String senderName = "LogiTracker";

        String mailContent = "Hi, " +" \n"+ user.getFullName() + "\n" +
                "Thank you for registering with us," +
                "Please, follow the link below to complete your registration." +
                "\n" +
                confirmationLink + " \nVerify your email to activate your account" +
                "\nThank you.";

        SimpleMailMessage mailMessage = new SimpleMailMessage();
        mailMessage.setTo(user.getEmail());
        mailMessage.setSubject(subject);
        mailMessage.setFrom("logitrackapplication@gmail.com" + senderName);
        mailMessage.setText(mailContent);
        sendEmail(mailMessage);
    }

}
