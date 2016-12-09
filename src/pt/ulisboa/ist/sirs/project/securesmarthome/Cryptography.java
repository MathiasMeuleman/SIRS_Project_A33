package pt.ulisboa.ist.sirs.project.securesmarthome;

/**
 * Created by Robert on 11/28/16.
 */

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import java.security.Key;

public class Cryptography {

    private final static String ALGO = "AES";
    private final static String MODE = "ECB";
    private final static String PADDING = "PKCS5Padding";
    private static IvParameterSpec ivVector;

    public static byte[] encrypt(byte[] plaintext, Key key) {
        return encrypt(plaintext, key, MODE);
    }

    public static byte[] encrypt(byte[] plaintext, Key key, String mode) {
        byte[] ciphertext;
        try {
            Cipher c = Cipher.getInstance(ALGO+"/"+mode+"/"+PADDING);
            if(mode.equals("CBC")) {
                c.init(Cipher.ENCRYPT_MODE, key, ivVector);
            } else {
                c.init(Cipher.ENCRYPT_MODE, key);
            }
            ciphertext = c.doFinal(plaintext);
            return ciphertext;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static byte[] decrypt(byte[] ciphertext, Key key) {
        return decrypt(ciphertext, key, MODE);
    }

    public static byte[] decrypt(byte[] ciphertext, Key key, String mode) {
        byte[] plaintext;
        try {
            Cipher c = Cipher.getInstance(ALGO+"/"+mode+"/"+PADDING);
            if(mode.equals("CBC")) {
                c.init(Cipher.DECRYPT_MODE, key, ivVector);
            } else {
                c.init(Cipher.DECRYPT_MODE, key);
            }
            plaintext = c.doFinal(ciphertext);
            return plaintext;
        } catch (BadPaddingException e) {
            System.out.println("Wrong key used for decryption!");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void setIV(byte[] iv) {
        ivVector = new IvParameterSpec(iv);
    }
}
