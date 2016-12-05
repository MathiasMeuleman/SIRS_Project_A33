package pt.ulisboa.ist.sirs.project.securesmarthome;

/**
 * Created by maxwell on 11/28/16.
 */
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyGenerator;

import java.security.*;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;

public class Cryptography {

    private final static String ALGO = "AES";
    private final static String MODE = "ECB";
    private final static String PADDING = "PKCS5Padding";
    private static IvParameterSpec ivVector;

    public static void main(String args[]) {
        try {
            byte[] plaintext = {(byte) 0x30, (byte) 0x31, (byte) 0x32};
            AESSecretKeyGenerator keyGenerator = new AESSecretKeyGenerator();
            SecretKey key = keyGenerator.generateKey();
            System.out.println("plaintext: " + new String(plaintext, "UTF8"));
            byte[] cryptogramm = encrypt(plaintext, key);
            System.out.println("cryptogramm: " + new String(cryptogramm, "UTF8"));
            byte[] recoveredPlaintext = decrypt(cryptogramm, key);
            System.out.println("recoveredPlaintext: " + new String(recoveredPlaintext, "UTF8"));
            if (!java.util.Arrays.equals(plaintext, recoveredPlaintext))
                System.out.println("DES in CBC mode recovered text is " +
                        "different from cleartext");
            else
                System.out.println("DES in ECB mode recovered text is " +
                        "same as cleartext");
        } catch (Exception e) {
            System.out.println("Exception: " + e);
        }
    }

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
