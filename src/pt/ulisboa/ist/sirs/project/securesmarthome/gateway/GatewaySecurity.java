package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.SecurityManager;
import pt.ulisboa.ist.sirs.project.securesmarthome.AESSecretKeyFactory;
import pt.ulisboa.ist.sirs.project.securesmarthome.DHKeyAgreement;

import java.net.SocketException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.UUID;

/**
 * Created by Mathias on 2016-12-03.
 */
public class GatewaySecurity extends SecurityManager {

    private String key;
    private UUID SHDuuid;
    private int threadIndex;

    public GatewaySecurity(GatewaySocketChannel channel, long timeRef) {
        this.commChannel = channel;
        if(clientOnSameIP())
            this.timeRef = 0;
        else
            this.timeRef = timeRef;
        System.out.println("timeRef: " + this.timeRef);
    }

    @Override
    public void shareUUID() {
        System.out.println("waiting for UUID");
        byte[] unsec = receiveUnsecured();
        long most = Helper.bytesToLong(unsec);
        System.out.println("Got the first part: ");
        System.out.println(Arrays.toString(unsec));
        unsec = receiveUnsecured();
        long least = Helper.bytesToLong(unsec);
        System.out.println("Got the second part:");
        System.out.println(Arrays.toString(unsec));
        SHDuuid = new UUID(most, least);
        Gateway.linkThreadToUUID(threadIndex, SHDuuid);
        Gateway.newConnectionUUID = SHDuuid;
    }

    @Override
    public void shareSessionKey() {
        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(key);
        Gateway.aprioriSharedKeysList.put(SHDuuid, key);

        // do DH to establish shared key
        receivePubKey();
        publicGatewayKey = DHKeyAgreement.getPublicGatewayKey(publicSHDKey);
        sendPubKey();
        DHKeyAgreement.createSharedSecretB(publicSHDKey);
        sessionKey = DHKeyAgreement.getSharedSecretKey();
    }

    @Override
    public void shareIV() {
        byte[] iv = null;
        while(iv == null) {
            iv = generateRandomIV();
        }
        System.out.println("Generated IV: " + Arrays.toString(iv));
        sendEncrypted(iv, "ECB");
        Cryptography.setIV(iv);
    }

    @Override
    public void authenticate() {
        // receive authentication message from SHD
        byte[] authenticationMessage = receiveEncrypted(aprioriSharedKey, "CBC");
        System.out.println("Authstream: " + Arrays.toString(authenticationMessage));
        if (authenticationMessage == null) {
            // wrong key!!!
            Gateway.smartHomeDevices.add(new AuthenticatedSHD(false));
            System.err.println("[ERROR] SHD authentication failed!");
            commChannel.dropConnection();
            System.out.println("Drop connection to that SHD!");
        } else {
            // compare with concatenated keys
            byte[] concatKeys = Helper.getConcatPubKeys(publicSHDKey, publicGatewayKey);
            if (Arrays.equals(authenticationMessage, concatKeys)) {
                Gateway.smartHomeDevices.add(new AuthenticatedSHD(true));
                // authenticate gateway
                // generate authentication message
                authenticationMessage = Helper.getConcatPubKeys(publicGatewayKey, publicSHDKey);
                // authenticate by sending it to the other party
                sendEncrypted(authenticationMessage, aprioriSharedKey, "CBC");

                System.out.println("Gateway: SHD authentication succeed!");
            }
            else {
                // wrong public keys used for authentication
                System.out.println("Wrong public keys used for authentication!");
                Gateway.smartHomeDevices.add(new AuthenticatedSHD(false));
                System.err.println("[ERROR] SHD authentication failed!");
                commChannel.dropConnection();
                System.out.println("Drop connection to that SHD!");
            }
        }
    }

    protected byte[] generateRandomIV() {
        SecureRandom rand = new SecureRandom();
        byte[] iv = new byte[16];
        rand.nextBytes(iv);
        return iv;
    }

    @Override
    public void checkKeyExpired() throws SocketException {
        keyUsageCounter++;
        if(keyUsageCounter > KEY_THRESHOLD) {
            System.out.println("Session key expired");
            System.out.println("Dropping connection...");
            commChannel.dropConnection();
            throw new SocketException();
        }
    }

    protected void sendPubKey() {
        sendUnsecured(publicGatewayKey);
    }

    protected void receivePubKey() {
        publicSHDKey = receiveUnsecured();
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setThreadIndex(int threadIndex) {
        this.threadIndex = threadIndex;
    }
}
