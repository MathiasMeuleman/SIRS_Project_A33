//package pt.ulisboa.ist.sirs.project.securesmarthome.encryption;
//
//import javax.crypto.Cipher;
//import javax.crypto.SecretKey;
//import java.security.AlgorithmParameters;
//
///**
// * Created by Alex Anders on 21/11/2016.
// */
//public class Encryption {
//    /*
//         * Now let's return the shared secret as a SecretKey object
//         * and use it for encryption. First, we generate SecretKeys for the
//         * "DES" algorithm (based on the raw shared secret data) and
//         * then we use DES in ECB mode
//         * as the encryption algorithm. DES in ECB mode does not require any
//         * parameters.
//         *
//         * Then we use DES in CBC mode, which requires an initialization
//         * vector (IV) parameter. In CBC mode, you need to initialize the
//         * Cipher object with an IV, which can be supplied using the
//         * javax.crypto.spec.IvParameterSpec class. Note that you have to use
//         * the same IV for encryption and decryption: If you use a different
//         * IV for decryption than you used for encryption, decryption will
//         * fail.
//         *
//         * NOTE: If you do not specify an IV when you initialize the
//         * Cipher object for encryption, the underlying implementation
//         * will generate a random one, which you have to retrieve using the
//         * javax.crypto.Cipher.getParameters() method, which returns an
//         * instance of java.security.AlgorithmParameters. You need to transfer
//         * the contents of that object (e.g., in encoded format, obtained via
//         * the AlgorithmParameters.getEncoded() method) to the party who will
//         * do the decryption. When initializing the Cipher for decryption,
//         * the (reinstantiated) AlgorithmParameters object must be passed to
//         * the Cipher.init() method.
//         */
//        System.out.println("Return shared secret as SecretKey object ...");
//    // Bob
//    // NOTE: The call to bobKeyAgree.generateSecret above reset the key
//    // agreement object, so we call doPhase again prior to another
//    // generateSecret call
//        bobKeyAgree.doPhase(alicePubKey, true);
//    SecretKey bobDesKey = bobKeyAgree.generateSecret("DES");
//
//    // Alice
//    // NOTE: The call to aliceKeyAgree.generateSecret above reset the key
//    // agreement object, so we call doPhase again prior to another
//    // generateSecret call
//        aliceKeyAgree.doPhase(bobPubKey, true);
//    SecretKey aliceDesKey = aliceKeyAgree.generateSecret("DES");
//
//    /*
//     * Bob encrypts, using DES in ECB mode
//     */
//    Cipher bobCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//        bobCipher.init(Cipher.ENCRYPT_MODE, bobDesKey);
//
//    byte[] cleartext = "This is just an example".getBytes();
//    byte[] ciphertext = bobCipher.doFinal(cleartext);
//
//    /*
//     * Alice decrypts, using DES in ECB mode
//     */
//    Cipher aliceCipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
//        aliceCipher.init(Cipher.DECRYPT_MODE, aliceDesKey);
//    byte[] recovered = aliceCipher.doFinal(ciphertext);
//
//        if (!java.util.Arrays.equals(cleartext, recovered))
//            throw new Exception("DES in CBC mode recovered text is " +
//                                        "different from cleartext");
//        System.out.println("DES in ECB mode recovered text is " +
//                "same as cleartext");
//
//        /*
//         * Bob encrypts, using DES in CBC mode
//         */
//    bobCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        bobCipher.init(Cipher.ENCRYPT_MODE, bobDesKey);
//
//    cleartext = "This is just an example".getBytes();
//    ciphertext = bobCipher.doFinal(cleartext);
//    // Retrieve the parameter that was used, and transfer it to Alice in
//    // encoded format
//    byte[] encodedParams = bobCipher.getParameters().getEncoded();
//
//    /*
//     * Alice decrypts, using DES in CBC mode
//     */
//    // Instantiate AlgorithmParameters object from parameter encoding
//    // obtained from Bob
//    AlgorithmParameters params = AlgorithmParameters.getInstance("DES");
//        params.init(encodedParams);
//    aliceCipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
//        aliceCipher.init(Cipher.DECRYPT_MODE, aliceDesKey, params);
//    recovered = aliceCipher.doFinal(ciphertext);
//
//        if (!java.util.Arrays.equals(cleartext, recovered))
//            throw new Exception("DES in CBC mode recovered text is " +
//                                        "different from cleartext");
//        System.out.println("DES in CBC mode recovered text is " +
//                "same as cleartext");
//}
