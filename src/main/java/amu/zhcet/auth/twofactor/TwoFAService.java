package amu.zhcet.auth.twofactor;

import amu.zhcet.data.user.User;
import amu.zhcet.data.user.UserNotFoundException;
import amu.zhcet.data.user.UserService;
import lombok.extern.slf4j.Slf4j;
import org.jboss.aerogear.security.otp.Totp;
import org.jboss.aerogear.security.otp.api.Base32;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

@Slf4j
@Service
public class TwoFAService {

    private static String QR_PREFIX = "https://chart.googleapis.com/chart?chs=200x200&chld=M%%7C0&cht=qr&chl=";

    private final UserService userService;

    TwoFAService(UserService userService) {
        this.userService = userService;
    }

    public String generate2FASecret() throws UnsupportedEncodingException {
        User user = userService.getLoggedInUser().orElseThrow(UserNotFoundException::new);

        if (user.getTotpSecret() != null) {
            log.warn("User {} is overwriting TOTP with new one", user.getUserId());
        }

        String secret = Base32.random();
        user.setTotpSecret(secret);
        userService.save(user);

        log.debug("Adding secret {} to user {}", secret, user.getUserId());
        String url = generateQRUrl(user.getUserId(), secret);
        log.debug("QR code URL: {}", url);
        return url;
    }

    public void enable2FA(String code) {
        User user = userService.getLoggedInUser().orElseThrow(UserNotFoundException::new);

        if (verifyTotp(user.getTotpSecret(), code)) {
            user.setUsing2fa(true);
        } else {
            throw new RuntimeException("Could not verify code, please try again");
        }

        userService.save(user);
    }

    public static boolean verifyTotp(String totpSecret, String code) {
        if (totpSecret == null)
            throw new RuntimeException("Cannot get TOTP secret from user");
        Totp totp = new Totp(totpSecret);
        try {
            return totp.verify(code);
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    private String generateQRUrl(String userId, String secret) throws UnsupportedEncodingException {
        return QR_PREFIX + URLEncoder.encode(String.format(
                "otpauth://totp/%s?secret=%s&issuer=ZHCET", userId, secret), "UTF-8");
    }

}
