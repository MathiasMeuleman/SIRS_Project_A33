package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.SecurityManager;
import pt.ulisboa.ist.sirs.project.securesmarthome.DHKeyAgreement;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;

import java.util.Arrays;

/**
 * Created by Mathias on 2016-12-03.
 */
public class SHDSecurity extends SecurityManager {

    // This 16 byte key is printed on the smartHomeDevice
    String printedKey = "ABCDEFGHIJKLMNOP";

    public SHDSecurity() {
        this.commChannel = new SHDSocketChannel();
        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(printedKey);
    }

    @Override
    public void shareSessionKey() {
        publicSHDKey = DHKeyAgreement.getPublicSHDKey("-gen");
        sendPubKey();
        receivePubKey();
        DHKeyAgreement.createSharedSecretA(publicGatewayKey);
        sessionKey = DHKeyAgreement.getSharedSecretKey();
        System.out.println("Finished DH");
    }

    @Override
    public void shareIV() {
        byte[] iv = receiveEncrypted("ECB");
        Cryptography.setIV(iv);
    }

    @Override
    public void authenticate()
    {
        // generate authentication message
        byte[] authenticationMessage = Helper.getConcatPubKeys(publicSHDKey, publicGatewayKey);
        // authenticate by sending it to the other party
        sendEncrypted(authenticationMessage, aprioriSharedKey, "CBC");
        // receive authentication message from Gateway
        authenticationMessage = receiveEncrypted(aprioriSharedKey, "CBC");
        if (authenticationMessage == null)
        {
            // wrong key!!!
            System.out.println("SHD: Gateway authentication failed!");
        }
        else {
            // compare with concatenated keys
            byte[] concatKeys = Helper.getConcatPubKeys(publicGatewayKey, publicSHDKey);
            if (Arrays.equals(authenticationMessage, concatKeys)) {
                System.out.println("SHD: Gateway authentication succeed!");
            }
            else {
                // wrong public keys used for authentication
                System.out.println("Wrong public keys used for authentication!");
                System.out.println("SHD: Gateway authentication failed!");
            }
        }
    }

    protected void sendPubKey() {
        sendUnsecured(publicSHDKey);
    }

    protected void receivePubKey() {
        publicGatewayKey = receiveUnsecured();
    }
}
