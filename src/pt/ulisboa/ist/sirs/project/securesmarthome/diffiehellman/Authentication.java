package pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import javax.crypto.SecretKey;

/**
 * Created by Alex Anders on 28/11/2016.
 */
public class Authentication {

    public static byte[] getConcatPubKeyABEncrypted(SecretKey aprioriSharedKey, byte[] pubKeyEncA, byte[] pubKeyEncB) {

        // concatenating the public values
        byte[] concatPubKeyAB = new byte[pubKeyEncA.length + pubKeyEncB.length];
        System.arraycopy(pubKeyEncA, 0, concatPubKeyAB, 0, pubKeyEncA.length);
        System.arraycopy(pubKeyEncB, 0, concatPubKeyAB, pubKeyEncA.length, pubKeyEncB.length);

        // encrypting concatenated public values
        byte[] concatPubKeyABEncrypted = Cryptography.encrypt(concatPubKeyAB, aprioriSharedKey);
        return concatPubKeyABEncrypted;
    }

    public static byte[] getConcatPubKeyBAEncrypted(SecretKey aprioriSharedKey, byte[] pubKeyEncA, byte[] pubKeyEncB) {
        return getConcatPubKeyABEncrypted(aprioriSharedKey, pubKeyEncB, pubKeyEncA);
    }
}
