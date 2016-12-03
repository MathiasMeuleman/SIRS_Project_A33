package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SHDSocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreementSHD;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;

import java.util.Arrays;


/**
 * Created by Mathias on 2016-11-21.
 */
public class SmartHomeDevice extends Device {

    public SmartHomeDevice() {
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
        dhSharedSecretKey = DHKeyAgreement2.getSharedSecretKey();
        System.out.println("Finished DH");

        // authentication part
        authenticateGateway();
    }

    public void authenticateGateway()
    {
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
        Thread t = new Thread(this::temperatureSim);
        t.start();
    }


    private void temperatureSim() {
        //Simulate a temperature device :)
        int temp = 10;
        for (int i = 0; i <= 12; i++) {
            String data = "" + temp;
            byte[] dataBytes = data.getBytes();
            byte[] toSend = addTimestamp(dataBytes);
            byte[] encrypted = Cryptography.encrypt(toSend, dhSharedSecretKey);
            commChannel.sendMessage("localhost:11000", encrypted);
            temp++;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < 2; i++) {
            String data = "" + temp;
            byte[] dataBytes = data.getBytes();
            byte[] toSend = addPhonyTimestamp(dataBytes);
            byte[] encrypted = Cryptography.encrypt(toSend, dhSharedSecretKey);
            commChannel.sendMessage("localhost:11000", encrypted);
        }
        for (int i = 0; i <= 12; i++) {
            String data = "" + temp;
            byte[] dataBytes = data.getBytes();
            byte[] toSend = addTimestamp(dataBytes);
            byte[] encrypted = Cryptography.encrypt(toSend, dhSharedSecretKey);
            commChannel.sendMessage("localhost:11000", encrypted);
            temp--;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(true);
    }

    /**
     * Prepend current timestamp to the data
     * @param data
     * @return
     */
    public byte[] addTimestamp(byte[] data) {
        long timestamp = System.currentTimeMillis();
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
        long timestamp = System.currentTimeMillis() + 1200000;
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
        commChannel.sendMessage("localhost:11000", pubEncryptedSHDKey);
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
