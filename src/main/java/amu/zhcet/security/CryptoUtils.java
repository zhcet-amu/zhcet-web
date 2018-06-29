package amu.zhcet.security;

import com.google.common.hash.Hashing;
import org.apache.commons.codec.binary.Hex;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import java.nio.charset.Charset;
import java.security.SecureRandom;
import java.util.concurrent.atomic.AtomicBoolean;

public class CryptoUtils {

    private static final AtomicBoolean PEPPER_SET = new AtomicBoolean();
    private static String PEPPER = "some_nice_pepper";

    private static final String CRYPTO_TAG_1 = "{crypto1}";
    private static final String CURRENT_CRYPTO_TAG = CRYPTO_TAG_1;

    private CryptoUtils() {}


    /**
     * Gets a standard data encryptor to be used within application
     *
     * Subject to discussion with peers about which fields should be used as password. Pepper is used as salt
     * Once the field is encrypted, it can't be decrypted if the internal logic of the function is changed,
     * Thus, it is very important to use the function judiciously
     *
     * If the function logic seems insecure or inadequate, you may create your own Encryptor.
     * For future proof encryption decryption, use the methods instead of the encryptor returned
     *
     * @param password String to be used as password
     * @return A text encryptor
     */
    public static TextEncryptor getStandardEncryptor(String password) {
        String salt = Hex.encodeHexString(PEPPER.getBytes());
        return Encryptors.delux(PEPPER + password + PEPPER, salt);
    }

    /**
     * Encrypts text with provided password using the getStandardEncryptor method.
     * Also, appends the the current crypto tag so that decryption can be future proof and only sent to correct decryptor
     *
     * @param data String data to be encrypted
     * @param password String password to encrypt data with
     * @return String encrypted data
     */
    public static String encrypt(String data, String password) {
        return CURRENT_CRYPTO_TAG + getStandardEncryptor(password).encrypt(data);
    }

    /**
     * Decrypts data according to the tag appended in front of it.
     * Checks which version of encryption mechanism has been used and dispatches decryption to that version of method.
     * The tag is first removed from the data sent to be decrypted.
     *
     * If no tag is matched, then the latest decryption method is used.
     * @param cipher String cipher to be decrypted
     * @param password String password to be used in decryption
     * @return String decrypted data
     */
    public static String decrypt(String cipher, String password) {
        if (cipher.startsWith(CURRENT_CRYPTO_TAG)) {
            String normalizedCipher = cipher.substring(CURRENT_CRYPTO_TAG.length());
            return getStandardEncryptor(password).decrypt(normalizedCipher);
        } else if (cipher.startsWith(CRYPTO_TAG_1)) {
            String normalizedCipher = cipher.substring(CRYPTO_TAG_1.length());
            return getStandardEncryptor(password).decrypt(normalizedCipher);
        } else {
            return getStandardEncryptor(password).decrypt(cipher);
        }
    }

    public static String getHash(String string) {
        return Hashing.sha256()
                .newHasher()
                .putString(PEPPER + string + PEPPER, Charset.defaultCharset())
                .hash()
                .toString();
    }

    public static boolean hashMatches(String string, String hash) {
        return getHash(string).equals(hash);
    }

    public static String generatePassword(int length) {
        char[] possibleCharacters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#_-?%&*"
                        .toCharArray();
        return RandomStringUtils.random(length,
                0, possibleCharacters.length - 1,
                false, false,
                possibleCharacters, new SecureRandom());
    }

    public static String getPepper() {
        return PEPPER;
    }

    public static void setPepper(String pepper) {
        if (PEPPER_SET.compareAndSet(false, true))
            PEPPER = pepper;
        else
            throw new IllegalStateException("Pepper can only be set once");
    }

}
