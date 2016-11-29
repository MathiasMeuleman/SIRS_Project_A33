package pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.*;
import java.io.*;
import java.security.spec.InvalidKeySpecException;

/**
 * Created by Mathias on 2016-11-21.
 */
public class AESSecretKeyGenerator {

    public static SecretKey generateKey() {
        try {
            KeyGenerator generator;
            generator = KeyGenerator.getInstance("AES");
            generator.init(128);
            return generator.generateKey();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }
}
