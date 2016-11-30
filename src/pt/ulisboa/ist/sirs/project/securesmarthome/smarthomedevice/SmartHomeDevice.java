package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreementSHD;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;

import java.util.Arrays;


/**
 * Created by Mathias on 2016-11-21.
 */
public class SmartHomeDevice extends Device{

    public SmartHomeDevice(CommunicationMode commMode) {
        super(commMode);

        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(printedKey);

        dh = new DHKeyAgreementSHD();
        dhKeyAgreement();
        System.out.println("Finished DH");
        // authentication part
        // generate authentication message
        authenticationMessage = Helper.getConcatPubKeys(pubKeyEncA, pubKeyEncB);
        // encrypt authentication message
        authenticationMessageEncrypted = Cryptography.encrypt(authenticationMessage, aprioriSharedKey);
        // authenticate by sending it to the other party
        commChannel.sendMessage(authenticationMessageEncrypted);
        // receive authentication message from Gateway
        authenticationMessageEncrypted = commChannel.receiveByteArray();
        // decrypt it
        authenticationMessage = Cryptography.decrypt(authenticationMessageEncrypted,aprioriSharedKey);
        // compare with concatenated keys
        byte[] concatKeys = Helper.getConcatPubKeys(pubKeyEncB, pubKeyEncA);
        if (Arrays.equals(authenticationMessage, concatKeys)) {
            authenticatedGateway = true;
        }
        else
            authenticatedGateway = false;
    }

    // This 16 byte key is printed on the smartHomeDevice
    String printedKey = "ABCDEFGHIJKLMNOP";

    private boolean authenticatedGateway;
}
