package pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement;

import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by Alex Anders on 29/11/2016.
 */
public class AESSecretKeyFactory {
    public static SecretKey createSecretKey(byte[] key) {
        try {
            SecretKeyFactory factory = SecretKeyFactory.getInstance("AES");
            return factory.generateSecret(new SecretKeySpec(key, "AES"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
