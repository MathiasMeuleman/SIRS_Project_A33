package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SHDSocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreementSHD;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;

import java.time.Instant;
import java.util.Arrays;


/**
 * Created by Mathias on 2016-11-21.
 */
public class PresentationClass extends Device {

    private Instant timestampReference;

    public PresentationClass() {
        super();
        this.id = counter;
        counter++;

        commChannel = new SHDSocketChannel();
        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(printedKey);

        // Get all public keys
        dh = new DHKeyAgreementSHD();
        pubEncryptedSHDKey = DHKeyAgreement2.getPubKeyEncSHD("-gen");
        sendPubKey();
        receivePubKey();
        dh.doDH(pubEncryptedGatewayKey);
        sessionKey = DHKeyAgreement2.getSharedSecretKey();
        System.out.println("Finished DH");

        // authentication part
        authenticateGateway();
        makeRefTime();
    }

    public void authenticateGateway()
    {
        // generate authentication message
        authenticationMessage = Helper.getConcatPubKeys(pubEncryptedSHDKey, pubEncryptedGatewayKey);
        // encrypt authentication message
        authenticationMessageEncrypted = Cryptography.encrypt(authenticationMessage, aprioriSharedKey);
        // authenticate by sending it to the other party
        commChannel.sendMessage(authenticationMessageEncrypted);
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

    public void makeRefTime() {
        long reftime = Helper.bytesToLong(sessionKey.getEncoded());
        timestampReference = Instant.ofEpochMilli(reftime);
    }

    public void send(byte[] data) {
        byte[] toSend = addTimestamp(data);
        byte[] encrypted = Cryptography.encrypt(toSend, sessionKey);
        commChannel.sendMessage(encrypted);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Prepend current timestamp to the data
     * @param data
     * @return
     */
    public byte[] addTimestamp(byte[] data) {
        Instant inst = Instant.now();
        long timestamp = inst.toEpochMilli() - timestampReference.toEpochMilli();
        byte[] stampBytes = Helper.longToBytes(timestamp);
        int size = data.length + stampBytes.length;
        byte[] toSend = new byte[size];
        for (int i = 0; i < stampBytes.length; i++) {
            toSend[i] = stampBytes[i];
        }
        for (int i = 0; i < data.length; i++) {
            toSend[stampBytes.length + i] = data[i];
        }
        return toSend;
    }

    public byte[] addPhonyTimestamp(byte[] data) {
        Instant inst = Instant.now();
        long timestamp = inst.toEpochMilli() + 1200000;
        byte[] stampBytes = Helper.longToBytes(timestamp);
        int size = data.length + stampBytes.length;
        byte[] toSend = new byte[size];
        for (int i = 0; i < stampBytes.length; i++) {
            toSend[i] = stampBytes[i];
        }
        for (int i = 0; i < data.length; i++) {
            toSend[stampBytes.length + i] = data[i];
        }
        return toSend;
    }

    private void sendPubKey() {
        commChannel.sendMessage(pubEncryptedSHDKey);
    }

    private void receivePubKey() {
        pubEncryptedGatewayKey = commChannel.receiveByteArray();
    }

    public int getId() {
        return id;
    }

    // This 16 byte key is printed on the smartHomeDevice
    String printedKey = "ABCDEFGHIJKLMNOP";


    private static int counter = 0;
    private boolean authenticatedGateway;
    private int id;
}
