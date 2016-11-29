package pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureClassLoader;

/**
 * Created by Mathias on 2016-11-21.
 */
public class AESKeyGenerator {

    private KeyGenerator generator;

    public AESKeyGenerator() {
        try {
            generator = KeyGenerator.getInstance("AES");
            generator.init(128);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public SecretKey generateKey() {
        return generator.generateKey();
    }

//    public Key read(String keyPath) throws GeneralSecurityException, IOException {
//        System.out.println("Reading key from file " + keyPath + " ...");
//        FileInputStream fis = new FileInputStream(keyPath);
//        byte[] encoded = new byte[fis.available()];
//        fis.read(encoded);
//        fis.close();
//
//        return new SecretKeySpec(encoded, 0, 16, "AES");
//    }
}
