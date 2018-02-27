package amu.zhcet.auth.twofactor;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserNotFoundException;
import amu.zhcet.data.user.UserService;
import com.google.common.base.Strings;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Service
public class TwoFAService {

    private final UserService userService;

    TwoFAService(UserService userService) {
        this.userService = userService;
    }

    @Data
    @AllArgsConstructor
    static class TwoFASecret {
        private static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

        private String userId;
        private String secret;

        public String getQrUrl() {
            try {
                return QR_PREFIX + URLEncoder.encode(String.format("otpauth://totp/%s?secret=%s&issuer=ZHCET", userId, secret),
                        "UTF-8");
            } catch (UnsupportedEncodingException e) {
                return null;
            }
        }
    }

    /**
     * Generates a random secret to be be seed of TOTP secret and QR Code URL
     * @return {@link TwoFASecret} enclosing the user ID and secret
     */
    TwoFASecret generate2FASecret() {
        User user = userService.getLoggedInUser().orElseThrow(UserNotFoundException::new);

        if (user.getTotpSecret() != null) {
            log.warn("User {} is overwriting TOTP with new one", user.getUserId());
        }

        String secret = Base32.random();
        log.debug("Adding secret {} to user {}", secret, user.getUserId());
        TwoFASecret twoFASecret = new TwoFASecret(user.getUserId(), secret);
        log.debug("QR code URL: {}", twoFASecret.getQrUrl());
        return twoFASecret;
    }

    /**
     * Takes in the secret and OTP from frontend and enables 2 factor authentication if they are verified
     * @param secret String secret for the user
     * @param code String OTP code
     */
    void enable2FA(String secret, String code) {
        User user = userService.getLoggedInUser().orElseThrow(UserNotFoundException::new);

        if (!isValidOtp(secret, code)) {
            throw new RuntimeException("Could not verify code, please try again");
        }

        user.setUsing2fa(true);
        user.setTotpSecret(secret);
        userService.save(user);
    }

    /**
     * Unconditionally enables 2 Factor Authentication for the user if OTP secret is already present
     */
    void enable2FA() {
        User user = userService.getLoggedInUser().orElseThrow(UserNotFoundException::new);
        if (user.getTotpSecret() == null)
            return;
        user.setUsing2fa(true);
        userService.save(user);
    }

    void disable2FA() {
        User user = userService.getLoggedInUser().orElseThrow(UserNotFoundException::new);
        user.setUsing2fa(false);
        user.setTotpSecret(null);
        userService.save(user);
    }

    /**
     * Sanitizes the TOTP secret and TOTP and verifies if they are authentic or not
     * If the TOTP secret is below certain length, it is rejected as well and thought to be considered tampered
     * @param totpSecret Secret
     * @param code TOTP
     * @return boolean denoting if the passed in values are authentic
     */
    public static boolean isValidOtp(String totpSecret, String code) {
        if (Strings.isNullOrEmpty(totpSecret))
            throw new RuntimeException("Cannot get TOTP secret from user");
        if (totpSecret.length() != 16)
            throw new RuntimeException("TOTP Secret was tampered with");
        String refinedOtp = code.replace(" ", "");
        Totp totp = new Totp(totpSecret);
        try {
            return totp.verify(refinedOtp);
        } catch (NumberFormatException nfe) {
            return false;
        }
    }
}
