package amu.zhcet.security;

import org.hamcrest.CoreMatchers;
import org.junit.Test;
import org.springframework.security.crypto.encrypt.TextEncryptor;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

public class CryptoUtilsTest {

    @Test
    public void testStandardEncryptorNotNull() {
        TextEncryptor textEncryptor = CryptoUtils.getStandardEncryptor("newPassword");
        assertNotNull(textEncryptor);
    }

    @Test
    public void testStandardTextEncryptor() {
        TextEncryptor textEncryptor = CryptoUtils.getStandardEncryptor("newPassword");

        String dataToBeEncrypted = "This is a String data to be encrypted";
        String encryptedData = textEncryptor.encrypt(dataToBeEncrypted);

        assertNotNull(encryptedData);

        String decryptedData = textEncryptor.decrypt(encryptedData);

        assertNotNull(decryptedData);
        assertEquals(dataToBeEncrypted, decryptedData);
    }

    @Test
    public void testPrefixTextEncryptor() {
        String dataToBeEncrypted = "Here goes my data to be encrypted";
        String encryptedData = CryptoUtils.encrypt(dataToBeEncrypted, "myPassword");

        assertNotNull(encryptedData);
        assertThat(encryptedData, CoreMatchers.startsWith("{crypto1"));
    }

    @Test
    public void testTextDecryptor() {
        String dataToBeEncrypted = "Let's see how this data is encrypted!";
        String password = "strongPassword";
        String encryptedData = CryptoUtils.encrypt(dataToBeEncrypted, password);
        String decryptedData = CryptoUtils.decrypt(encryptedData, password);

        assertEquals(dataToBeEncrypted, decryptedData);
    }

    @Test
    public void testTextDecryptorOnNonMatchingTag() {
        String dataToBeEncrypted = "Pathogenic installments have plagued modern Greece in recent history";
        String password = "strongPassword";
        String encryptedData = CryptoUtils.getStandardEncryptor(password).encrypt(dataToBeEncrypted);
        String decryptedData = CryptoUtils.decrypt(encryptedData, password);

        assertEquals(dataToBeEncrypted, decryptedData);
    }

}