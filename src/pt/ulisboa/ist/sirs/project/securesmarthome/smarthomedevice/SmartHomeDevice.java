package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreementSHD;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;

import java.security.SecureRandom;
import java.util.Arrays;


/**
 * Created by Mathias on 2016-11-21.
 */
public class SmartHomeDevice extends Device{

    public SmartHomeDevice(CommunicationMode commMode) {
        super(commMode);

        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(printedKey);

        // Get all public keys
        dh = new DHKeyAgreementSHD();
        pubEncryptedSHDKey = DHKeyAgreement2.getPubKeyEncSHD("-gen");
        sendPubKey();
        receivePubKey();
        dh.doDH(pubEncryptedGatewayKey);
        dhSharedSecretKey = DHKeyAgreement2.getSharedSecretKey();
        System.out.println("Finished DH");

        // authentication part
        // generate authentication message
        authenticationMessage = Helper.getConcatPubKeys(pubEncryptedSHDKey, pubEncryptedGatewayKey);
        // encrypt authentication message
        authenticationMessageEncrypted = Cryptography.encrypt(authenticationMessage, aprioriSharedKey);
        // authenticate by sending it to the other party
        commChannel.sendMessage("localhost:11000",authenticationMessageEncrypted);
        // receive authentication message from Gateway
        authenticationMessageEncrypted = commChannel.receiveByteArray();
        // decrypt it
        authenticationMessage = Cryptography.decrypt(authenticationMessageEncrypted,aprioriSharedKey);
        if (authenticationMessage == null)
        {
            // wrong key!!!
            authenticatedGateway = false;
            System.out.println("SHD: Gateway authentication failed!");
        }
        else {
            // compare with concatenated keys
            byte[] concatKeys = Helper.getConcatPubKeys(pubEncryptedGatewayKey, pubEncryptedSHDKey);
            if (Arrays.equals(authenticationMessage, concatKeys)) {
                authenticatedGateway = true;
                System.out.println("SHD: Gateway authentication succeed!");
            }
            else {
                // wrong public keys used for authentication
                System.out.println("Wrong public keys used for authentication!");
                authenticatedGateway = false;
                System.out.println("SHD: Gateway authentication failed!");
            }
        }
    }

    public void run() {
        SecureRandom rand = new SecureRandom();
        byte[] data = new byte[32];
        while(true) {
            rand.nextBytes(data);
            byte[] encrypted = Cryptography.encrypt(data, dhSharedSecretKey);
            commChannel.sendMessage("localhost:11000", encrypted);
            System.out.println("Sending data");
        }
    }

    private void sendPubKey() {
        commChannel.sendMessage("localhost:11000", pubEncryptedSHDKey);
    }

    private void receivePubKey() {
        pubEncryptedGatewayKey = commChannel.receiveByteArray();
    }

    // This 16 byte key is printed on the smartHomeDevice
    String printedKey = "ABCDEFGHIJKLMNOP";

    private boolean authenticatedGateway;
}
