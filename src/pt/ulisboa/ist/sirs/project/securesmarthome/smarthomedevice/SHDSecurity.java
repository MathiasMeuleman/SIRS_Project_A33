package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.SecurityManager;
import pt.ulisboa.ist.sirs.project.securesmarthome.DHKeyAgreement;
import pt.ulisboa.ist.sirs.project.securesmarthome.AESSecretKeyFactory;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Mathias on 2016-12-03.
 */
public class SHDSecurity extends SecurityManager {

    // This 16 byte key is printed on the smartHomeDevice
    String printedKey = "ABCDEFGHIJKLMNOP";
    private UUID uuid;

    public SHDSecurity() {
        this.commChannel = new SHDSocketChannel();
        if(commChannel == null)
            System.out.println("COMMS == NULL");
        if(clientOnSameIP())
            timeRef = 0;
        else
            timeRef = Helper.initTimestamp();
        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(printedKey);
        System.out.println("timeRef: " + timeRef);
        shareUUID();
    }

    @Override
    public void shareUUID() {
        if(uuid ==  null) {
            SecureRandom rand = new SecureRandom();
            byte[] bytes = new byte[16];
            rand.nextBytes(bytes);
            ByteBuffer bb = ByteBuffer.wrap(bytes);
            uuid = new UUID(bb.getLong(), bb.getLong());
        }
        System.out.println("Sending first part: " + Arrays.toString(Helper.longToBytes(uuid.getMostSignificantBits())));
        sendUnsecured(Helper.longToBytes(uuid.getMostSignificantBits()));
        System.out.println("Sending second part: " + Arrays.toString(Helper.longToBytes(uuid.getLeastSignificantBits())));
        sendUnsecured(Helper.longToBytes(uuid.getLeastSignificantBits()));
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
        System.out.println("IV from Gateway: " + Arrays.toString(iv));
        Cryptography.setIV(iv);
    }

    @Override
    public void authenticate()
    {
        // generate authentication message
        byte[] authenticationMessage = Helper.getConcatPubKeys(publicSHDKey, publicGatewayKey);
        System.out.println("Authstream: " + Arrays.toString(authenticationMessage));
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

    @Override
    public void checkKeyExpired() {}

    public void resetConnection() {
        this.commChannel.dropConnection();
        this.commChannel = new SHDSocketChannel();
        if(this.commChannel != null)
            System.out.println("I got a new connection!!");
        shareUUID();
        System.out.println("[INfO] Shared UUID");
        shareSessionKey();
        System.out.println("[INFO] Shared session key");
        shareIV();
        System.out.println("[INFO] Shared IV");
        authenticate();
    }

    protected void sendPubKey() {
        sendUnsecured(publicSHDKey);
    }

    protected void receivePubKey() {
        publicGatewayKey = receiveUnsecured();
    }
}
