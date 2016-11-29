package pt.ulisboa.ist.sirs.project.securesmarthome.encryption;

/**
 * Created by maxwell on 11/28/16.
 */
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyGenerator;

import java.security.*;

import java.io.*;
import javax.crypto.*;

public class Cryptography {

    private final static String ALGO = "AES";
    private final static String MODE = "ECB";
    private final static String PADDING = "PKCS5Padding";


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
        byte[] ciphertext = null;

        try {
            System.out.println("Start encryption ...");

    /* Get Message to be sent */
//                String input = getMessage();
            System.out.println("Message to encrypt: " + plaintext);

    /* Create Secret Key */
    /* Needs to change for getSharedKey */
//            KeyGenerator keyGen = KeyGenerator.getInstance(ALGO);
//            SecureRandom random = SecureRandom.getInstance("SHA1PRNG", "SUN");
//            keyGen.init(56,random);
//            Key sharedKey = keyGen.generateKey();

    /* Create the Cipher and init it with the secret key */
            Cipher c = Cipher.getInstance(ALGO+"/"+MODE+"/"+PADDING);
            //System.out.println("\n" + c.getProvider().getInfo());
            c.init(Cipher.ENCRYPT_MODE, key);
            ciphertext = c.doFinal(plaintext);
//            System.out.println("Message encrypted : " + new String(ciphertext,"UTF8"));
            System.out.println("Message encrypted : " + ciphertext);

    /* Save key to a file */
//            save(key.getEncoded(),"shared.key");

    /* Save encrypted data to a file */
//            save(ciphertext,"encrypted.txt");

        } catch (Exception e) {
            e.printStackTrace();
        }

        return ciphertext;
    }

    public static byte[] decrypt(byte[] ciphertext, Key key) {

        byte[] plaintext = null;

        try {
            System.out.println("Start decryption ...");

    /* Get encoded shared key from file*/
//            byte[] encoded = load("shared.key");
//            SecretKeyFactory kf = SecretKeyFactory.getInstance(ALGO);
//            KeySpec ks = new DESKeySpec(encoded);
//            SecretKey ky = kf.generateSecret(ks);

    /* Get encoded data */
//            byte[] ciphertext = load("encrypted.txt");
//            System.out.println("Encoded data = " + new String(ciphertext,"UTF8"));
            System.out.println("Encoded data = " + ciphertext);

    /* Create a Cipher object and initialize it with the secret key */
            Cipher c = Cipher.getInstance(ALGO+"/"+MODE+"/"+PADDING);
            c.init(Cipher.DECRYPT_MODE,key);

    /* Update and decrypt */
            plaintext = c.doFinal(ciphertext);
//            System.out.println("Plain Text : "+new String(plaintext,"UTF8"));
            System.out.println("Plain Text : " + plaintext);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return plaintext;
    }

    private static String getMessage() {
        String dest = "localhost:port";
        String message = "Message I want to send";
        final String rc = System.getProperty("line.separator");
        StringBuffer buf = new StringBuffer();
        buf.append(dest);
        buf.append(rc);
        buf.append(message);
        return buf.toString();
    }

    /* Writing encrypted text in a file */
    private static void save(byte[] buf, String file) throws IOException {
        FileOutputStream fos = new FileOutputStream(file);
        fos.write(buf);
        fos.close();
    }

    /* Reading from a file to decrypt */
    private static byte[] load(String file) throws FileNotFoundException, IOException {
        FileInputStream fis = new FileInputStream(file);
        byte[] buf = new byte[fis.available()];
        fis.read(buf);
        fis.close();
        return buf;
    }
}

/*
In case we want to use CBC mode we have some example below
 */
//        /*
//         * Bob encrypts, using DES in CBC mode
//         */
//        bobCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        bobCipher.init(Cipher.ENCRYPT_MODE, bobDesKey);
//
//        cleartext = "This is just an example".getBytes();
//        ciphertext = bobCipher.doFinal(cleartext);
//        // Retrieve the parameter that was used, and transfer it to Alice in
//        // encoded format
//        byte[] encodedParams = bobCipher.getParameters().getEncoded();
//
//    /*
//     * Alice decrypts, using DES in CBC mode
//     */
//        // Instantiate AlgorithmParameters object from parameter encoding
//        // obtained from Bob
//        AlgorithmParameters params = AlgorithmParameters.getInstance("DES");
//        params.init(encodedParams);
//        aliceCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        aliceCipher.init(Cipher.DECRYPT_MODE, aliceDesKey, params);
//        recovered = aliceCipher.doFinal(ciphertext);
//
//        if (!java.util.Arrays.equals(cleartext, recovered))
//            throw new Exception("DES in CBC mode recovered text is " +
//                    "different from cleartext");
//        System.out.println("DES in CBC mode recovered text is " +
//                "same as cleartext");
//    }