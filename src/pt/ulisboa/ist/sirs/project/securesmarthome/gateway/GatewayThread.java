package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.GatewaySocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreementGateway;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Arrays;

/**
 * Created by Mathias on 2016-12-03.
 */
public class GatewayThread extends Thread {

    public static final long TIMESTAMP_THRESHOLD = 500;
    private Instant timestampReference;

    private GatewaySocketChannel commChannel;
    private String key;
    private SecretKey dhSharedSecretKey;
    private byte[] pubEncryptedSHDKey;
    private byte[] pubEncryptedGatewayKey;

    public GatewayThread(GatewaySocketChannel commChannel, String key) {
        this.commChannel = commChannel;
        this.key = key;
    }

    @Override
    public void run() {
        addSHDToSHS(key, 0);
        tempSim();
    }

    public void addSHDToSHS(String aprioriSharedKey, int indexOfSHD) {
        // add the new input key to the list for authentication
        Gateway.aprioriSharedKeysList.put(indexOfSHD, AESSecretKeyFactory.createSecretKey(aprioriSharedKey));

        // do DH to establish shared key
        DHKeyAgreement dh = new DHKeyAgreementGateway();
        receivePubKey();
        pubEncryptedGatewayKey = DHKeyAgreement2.getPubKeyEncGW(pubEncryptedSHDKey);
        sendPubKey();
        dh.doDH(pubEncryptedSHDKey);
        this.dhSharedSecretKey = DHKeyAgreement2.getSharedSecretKey();
        System.out.println("Finished DH");

        // authentication part of station to station
        authenticateSHD(indexOfSHD);
        makeRefTime();
    }

    public void authenticateSHD(int indexOfSHD)
    {
        // receive authentication message from SHD
        byte[] authenticationMessageEncrypted = commChannel.receiveByteArray();
        // decrypt it
        byte[] authenticationMessage = Cryptography.decrypt(authenticationMessageEncrypted,
                Gateway.aprioriSharedKeysList.get(indexOfSHD));
        if (authenticationMessage == null) {
            // wrong key!!!
            Gateway.smartHomeDevices.add(new AuthenticatedSHD(false));
            System.out.println("Gateway: SHD authentication failed!");
            commChannel.gatewayDropConnection();
            System.out.println("Drop connection to that SHD!");
        } else {
            // compare with concatenated keys
            byte[] concatKeys = Helper.getConcatPubKeys(pubEncryptedSHDKey, pubEncryptedGatewayKey);
            if (Arrays.equals(authenticationMessage, concatKeys)) {
                Gateway.smartHomeDevices.add(new AuthenticatedSHD(true));
                // authenticate gateway
                // generate authentication message
                authenticationMessage = Helper.getConcatPubKeys(pubEncryptedGatewayKey, pubEncryptedSHDKey);
                // encrypt authentication message
                authenticationMessageEncrypted = Cryptography.encrypt(authenticationMessage,
                        Gateway.aprioriSharedKeysList.get(indexOfSHD));
                // authenticate by sending it to the other party
                commChannel.sendMessage("localhost:12005", authenticationMessageEncrypted);
                System.out.println("Gateway: SHD authentication succeed!");
            }
            else {
                // wrong public keys used for authentication
                System.out.println("Wrong public keys used for authentication!");
                Gateway.smartHomeDevices.add(new AuthenticatedSHD(false));
                System.out.println("Gateway: SHD authentication failed!");
                commChannel.gatewayDropConnection();
                System.out.println("Drop connection to that SHD!");
            }
        }
    }

    public void makeRefTime() {
        long reftime = Helper.bytesToLong(dhSharedSecretKey.getEncoded());
        timestampReference = Instant.ofEpochMilli(reftime);
        System.out.println("Timestampref: " + reftime);
    }

    public void tempSim() {
        while(true) {
            byte[] encrypted = commChannel.receiveByteArray();
            byte[] received = Cryptography.decrypt(encrypted, dhSharedSecretKey);
            long timestamp = retrieveTimestamp(received);
            if(checkTimestamp(timestamp)) {
                byte[] dataBytes = retrieveData(received);
                String data = new String(dataBytes);
                System.out.println("Timestamp: " + timestamp);
                System.out.println("Data: " + data);
            } else {
                System.err.println("Invalid timestamp detected: " + timestamp);
            }
        }
    }

    private byte[] retrieveData(byte[] received) {
        byte[] data = new byte[received.length - Long.BYTES];
        for (int i = 0; i < data.length; i++) {
            data[i] = received[i + Long.BYTES];
        }
        return data;
    }

    private long retrieveTimestamp(byte[] data) {
        byte[] stampBytes = new byte[Long.BYTES];
        for (int i = 0; i < Long.BYTES; i++) {
            stampBytes[i] = data[i];
        }
        return Helper.bytesToLong(stampBytes);
    }

    private boolean checkTimestamp(long timestamp) {
        Instant inst = Instant.now();
        long current = inst.toEpochMilli();
        if(current - timestamp > TIMESTAMP_THRESHOLD || timestamp > current)
            return false;
        return true;
    }

    private void receivePubKey() {
        pubEncryptedSHDKey = commChannel.receiveByteArray();
    }

    private void sendPubKey() {
        commChannel.sendMessage("localhost:12005", pubEncryptedGatewayKey);
    }
}
