package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreementGateway;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;

import javax.crypto.SecretKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Mathias on 2016-11-21.
 */
public class Gateway extends Device {

    public static final long TIMESTAMP_THRESHOLD = 500;

    public Gateway(CommunicationMode commMode, String key) {
        super(commMode);
        smartHomeDevices = new ArrayList<>();
        aprioriSharedKeysList = new ArrayList<>();

        // do this in a thread for each new SHD
        addSHDToSHS(key, 0);

        // either connection is established and the SHD is sending its data
        // or connection was dropped because of authentication issues
        // nevertheless a new channel needs to be setup
        if (!smartHomeDevices.get(0).isAuthenticated()) {
            System.out.println("New channel is setup.");
            commChannel.setup();
        }
    }


    public void addSHDToSHS(String aprioriSharedKey, int indexOfSHD) {
        // add the new input key to the list for authentication
        aprioriSharedKeysList.add(AESSecretKeyFactory.createSecretKey(aprioriSharedKey));

        // do DH to establish shared key
        dh = new DHKeyAgreementGateway();
        receivePubKey();
        pubEncryptedGatewayKey = DHKeyAgreement2.getPubKeyEncGW(pubEncryptedSHDKey);
        sendPubKey();
        dh.doDH(pubEncryptedSHDKey);
        dhSharedSecretKey = DHKeyAgreement2.getSharedSecretKey();
        System.out.println("Finished DH");

        // authentication part of station to station
        authenticateSHD(indexOfSHD);
    }

    public void authenticateSHD(int indexOfSHD)
    {
        // receive authentication message from SHD
        authenticationMessageEncrypted = commChannel.receiveByteArray();
        // decrypt it
        authenticationMessage = Cryptography.decrypt(authenticationMessageEncrypted,
                aprioriSharedKeysList.get(indexOfSHD));
        if (authenticationMessage == null) {
            // wrong key!!!
            smartHomeDevices.add(new AuthenticatedSHD(false));
            System.out.println("Gateway: SHD authentication failed!");
            commChannel.gatewayDropConnection();
            System.out.println("Drop connection to that SHD!");
        } else {
            // compare with concatenated keys
            byte[] concatKeys = Helper.getConcatPubKeys(pubEncryptedSHDKey, pubEncryptedGatewayKey);
            if (Arrays.equals(authenticationMessage, concatKeys)) {
                smartHomeDevices.add(new AuthenticatedSHD(true));
                // authenticate gateway
                // generate authentication message
                authenticationMessage = Helper.getConcatPubKeys(pubEncryptedGatewayKey, pubEncryptedSHDKey);
                // encrypt authentication message
                authenticationMessageEncrypted = Cryptography.encrypt(authenticationMessage,
                        aprioriSharedKeysList.get(indexOfSHD));
                // authenticate by sending it to the other party
                commChannel.sendMessage("localhost:12005", authenticationMessageEncrypted);
                System.out.println("Gateway: SHD authentication succeed!");
            }
            else {
                // wrong public keys used for authentication
                System.out.println("Wrong public keys used for authentication!");
                smartHomeDevices.add(new AuthenticatedSHD(false));
                System.out.println("Gateway: SHD authentication failed!");
                commChannel.gatewayDropConnection();
                System.out.println("Drop connection to that SHD!");
            }
        }
    }

    @Override
    public void run() {
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
        long current = System.currentTimeMillis();
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

    private List<AuthenticatedSHD> smartHomeDevices;

    private List<SecretKey> aprioriSharedKeysList;
}
