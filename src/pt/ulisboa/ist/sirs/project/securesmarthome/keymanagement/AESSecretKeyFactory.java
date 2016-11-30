package pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by Alex Anders on 29/11/2016.
 */
public class AESSecretKeyFactory {
    public static SecretKey createSecretKey(String key) {
        try {
            SecureRandom rand = new SecureRandom(key.getBytes(StandardCharsets.UTF_8));
            byte[] keyBytes = new byte[16];
            rand.nextBytes(keyBytes);
            return new SecretKeySpec(keyBytes, 0, 16, "AES");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
