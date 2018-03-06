package amu.zhcet.auth.verification;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserService;
import amu.zhcet.email.LinkMailService;
import amu.zhcet.email.LinkMessage;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
class EmailVerificationService {

    private final UserService userService;
    private final LinkMailService linkMailService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final Cache<String, LocalDateTime> emailCache;
    private final ApplicationEventPublisher eventPublisher;

    @Autowired
    public EmailVerificationService(UserService userService, LinkMailService linkMailService, VerificationTokenRepository verificationTokenRepository, ApplicationEventPublisher eventPublisher) {
        this.userService = userService;
        this.linkMailService = linkMailService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.eventPublisher = eventPublisher;

        this.emailCache = Caffeine.newBuilder()
                .maximumSize(10000)
                .expireAfterWrite(10, TimeUnit.MINUTES)
                .build();
    }

    private VerificationToken createVerificationToken(String email) {
        User user = userService.getLoggedInUser().orElseThrow(() -> new IllegalStateException("No user logged in"));
        // Check if link was already sent
        if (emailCache.getIfPresent(user.getUserId()) != null)
            throw new RuntimeException("Verification link was recently sent. Please wait for some time");

        emailCache.put(user.getUserId(), LocalDateTime.now());

        String token = UUID.randomUUID().toString();

        VerificationToken verificationToken = new VerificationToken(user, token, email);
        verificationTokenRepository.save(verificationToken);

        return verificationToken;
    }

    /**
     * Generates the verification token and sends the email to the specified user
     *
     * If any user with the same email exists, throws a {@link DuplicateEmailException}
     *
     * @param email String email for which the verification token is to be generated
     */
    public void generate(String email) {
        User loggedInUser = userService.getLoggedInUser()
                .orElseThrow(() -> new IllegalStateException("No user logged in"));
        Optional<User> userOptional = userService.getUserByEmail(email)
                .filter(user -> !user.getUserId().equals(loggedInUser.getUserId()));

        if (userOptional.isPresent()) {
            User user = userOptional.get();

            if (user.isEmailVerified()) {
                // Another user is present which has the same verified email
                // Throw exception notifying about the situation
                throw new DuplicateEmailException(email);
            } else {
                // The user has not yet verified his/her email and yet another user
                // asked for the same email, this may happen when the first user was
                // mischievous and just trying emails but couldn't verify, we will
                // transfer the email to current user

                log.warn("User {} has requested email {} which was requested already by {}",
                        loggedInUser, email, user);
                log.info("Resetting {} email and transferring to {}", user, loggedInUser);

                user.setEmail(null);
                eventPublisher.publishEvent(new DuplicateEmailEvent(loggedInUser, loggedInUser, email));
                userService.save(user);
            }
        }

        VerificationToken token = createVerificationToken(email);
        sendMail(token);
    }

    /**
     * Retrieves the verification token from database and validates it by performing checks
     * that it exists, is not already used and not expired. If any of the validation fails,
     * throws an {@link IllegalStateException} with the corresponding message
     * @param token String token ID
     * @return {@link VerificationToken}
     */
    private VerificationToken getAndValidateOrThrow(String token) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);

        if (verificationToken == null)
            throw new IllegalStateException("Token: " + token + " is invalid");

        if (verificationToken.isUsed())
            throw new IllegalStateException("Token: " + token + " is already used! Please request another link!");

        Calendar cal = Calendar.getInstance();
        if ((verificationToken.getExpiry().getTime() - cal.getTime().getTime()) <= 0)
            throw new IllegalStateException("Token: " + token + " has expired");

        return verificationToken;
    }

    /**
     * Validated a verification token and invalidates it by setting as used already and saves in the database
     * Then sets the new email to the user account and sets it to be verified and publishes an event stating same
     * @param token String token ID
     */
    @Transactional
    public void verifyEmail(String token) {
        VerificationToken verificationToken = getAndValidateOrThrow(token);

        verificationToken.setUsed(true);
        verificationTokenRepository.save(verificationToken);

        String email = verificationToken.getEmail();
        User user = verificationToken.getUser();

        Optional<User> userOptional = userService.getUserByEmail(email)
                .filter(us -> !us.getUserId().equals(user.getUserId()));

        if (userOptional.isPresent()) {
            // Some other user has the same email as in this token, this should not happen
            // But this is possible in the scenario of a verification lag. If that user is verified,
            // we will throw an exception and if that user is not verified, we will favour this user
            // and transfer the email from him/her to this user

            User duplicateEmailUser = userOptional.get();
            if (duplicateEmailUser.isEmailVerified()) {
                throw new DuplicateEmailException(email);
            } else {
                log.warn("User {} verified it's email {} owned by another user {}", user, email, duplicateEmailUser);
                duplicateEmailUser.setEmail(null);
                userService.save(user);
                eventPublisher.publishEvent(new DuplicateEmailEvent(user, duplicateEmailUser, email));
            }
        }

        user.setEmail(email);
        user.setEmailVerified(true);
        userService.save(user);
        eventPublisher.publishEvent(new EmailVerifiedEvent(user));
    }

    private LinkMessage getPayLoad(String recipientEmail, User user, String url) {
        return LinkMessage.builder()
                .recipientEmail(recipientEmail)
                .title("Email Verification")
                .subject("ZHCET Email Verification")
                .name(user.getName())
                .relativeLink(url)
                .linkText("Verify Account")
                .preMessage("You need to verify your email for user ID: " + user.getUserId() +
                        "<br>Please click the button below to verify your email and account")
                .postMessage("If you did not request the password reset, please contact website admin")
                .build();
    }

    /**
     * Sends mail to the user to be verified and saves the user with the email and status to be unverified
     * Also, publishes an event notifying the same
     * @param token {@link VerificationToken} token to be sent email with respect to
     */
    @Transactional
    public void sendMail(VerificationToken token) {
        String relativeUrl = "/login/email/verify?auth=" + token.getToken();
        log.debug("Verification link generated : {}", relativeUrl);

        linkMailService.sendEmail(getPayLoad(token.getEmail(), token.getUser(), relativeUrl), false);

        // Now we set the email to the user and disable email verified
        User user = token.getUser();
        log.debug("Saving user email {} -> {}", user.getUserId(), token.getEmail());
        user.setEmail(token.getEmail());
        user.setEmailVerified(false);

        userService.save(user);
        log.debug("Saved user email");
        eventPublisher.publishEvent(new EmailVerifiedEvent(user));
    }

}
