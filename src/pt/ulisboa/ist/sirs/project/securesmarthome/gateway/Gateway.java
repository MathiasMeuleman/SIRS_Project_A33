package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreementGateway;
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

    public Gateway(CommunicationMode commMode, String key) {
        super(commMode);
        smartHomeDevices = new ArrayList<>();
        aprioriSharedKeysList = new ArrayList<>();

        addSHDToSHS(key, 0);
    }

    public void addSHDToSHS(String aprioriSharedKey, int indexOfSHD) {
        // add the new input key to the list for authentication
        aprioriSharedKeysList.add(AESSecretKeyFactory.createSecretKey(aprioriSharedKey));

        dh = new DHKeyAgreementGateway();
        receivePubKey();
        pubEncryptedGatewayKey = DHKeyAgreement2.getPubKeyEncGW(pubEncryptedSHDKey);
        sendPubKey();
        dh.doDH(pubEncryptedSHDKey);
        dhSharedSecretKey = DHKeyAgreement2.getSharedSecretKey();
        System.out.println("Finished DH");

        // authentication part
        // receive authentication message from SHD
        authenticationMessageEncrypted = commChannel.receiveByteArray();
        // decrypt it
        authenticationMessage = Cryptography.decrypt(authenticationMessageEncrypted,
                aprioriSharedKeysList.get(indexOfSHD));
        if (authenticationMessage == null) {
            // wrong key!!!
            smartHomeDevices.add(new AuthenticatedSHD(false));
            System.out.println("Gateway: SHD authentication failed!");
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
            }
        }
        // FIXME for testing purposes
        // following 2 lines have to be deleted!
        authenticationMessageEncrypted = Cryptography.encrypt(authenticationMessage,
                aprioriSharedKeysList.get(indexOfSHD));
        commChannel.sendMessage("localhost:12005", authenticationMessageEncrypted);
    }

    @Override
    public void run() {
        while(true) {
            byte[] encrypted = commChannel.receiveByteArray();
            byte[] received = Cryptography.decrypt(encrypted, dhSharedSecretKey);
            byte[] stampBytes = new byte[Long.BYTES];
            byte[] dataBytes = new byte[received.length - Long.BYTES];
            for (int i = 0; i < Long.BYTES; i++) {
                stampBytes[i] = received[i];
            }
            long timestamp = Helper.bytesToLong(stampBytes);
            for (int i = 0; i < dataBytes.length; i++) {
                dataBytes[i] = received[i + Long.BYTES];
            }
            String data = new String(dataBytes);
            System.out.println("Timestamp: " + timestamp);
            System.out.println("Data: " + data);
        }
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
