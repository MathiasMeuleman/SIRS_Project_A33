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
import java.util.concurrent.TimeoutException;

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
    }

    @Override
    public void shareUUID() {
        System.out.println("waiting for UUID");
        byte[] unsec;
        try {
            unsec = receiveUnsecured();
        }
        catch (TimeoutException timeout)
        {
            System.out.println("Gateway: Timeout during first part UUID receiving!");
            commChannel.dropConnection();
            System.out.println("Gateway: Drop connection to that SHD!");
            return;
        }
        long most = Helper.bytesToLong(unsec);
        try {
            unsec = receiveUnsecured();
        }
        catch (TimeoutException timeout)
        {
            System.out.println("Gateway: Timeout during second part UUID receiving!");
            commChannel.dropConnection();
            System.out.println("Gateway: Drop connection to that SHD!");
            return;
        }
        long least = Helper.bytesToLong(unsec);
        System.out.println("Received UUID");
        SHDuuid = new UUID(most, least);
        Gateway.linkThreadToUUID(threadIndex, SHDuuid);
        Gateway.newConnectionUUID = SHDuuid;
    }

    @Override
    public void shareSessionKey() {
        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(key);
        Gateway.aprioriSharedKeysList.put(SHDuuid, key);
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
        System.out.println("Generated IV");
        sendEncrypted(iv, "ECB");
        Cryptography.setIV(iv);
    }

    @Override
    public void authenticate() {
        // receive authentication message from SHD
        byte[] authenticationMessage;
        try {
            authenticationMessage = receiveEncrypted(aprioriSharedKey, "CBC");
        }
        catch (TimeoutException timeout)
        {
            System.out.println("Gateway: SHD authentication failed!");
            System.out.println("Gateway: Timeout during authentication!");
            commChannel.dropConnection();
            System.out.println("Gateway: Drop connection to that SHD!");
            return;
        }
        if (authenticationMessage == null) {
            // wrong key!!!
            System.err.println("[ERROR] SHD authentication failed!");
            System.out.println("[ERROR] Wrong key used for encryption!");
            commChannel.dropConnection();
            System.out.println("Gateway: Drop connection to that SHD!");
        } else {
            // authenticate SHD
            // compare with concatenated keys
            byte[] concatKeys = Helper.getConcatPubKeys(publicSHDKey, publicGatewayKey);
            if (Arrays.equals(authenticationMessage, concatKeys)) {
                // SHD is authenticated
                System.out.println("Gateway: SHD authentication succeed!");

                // generate authentication message for gateway authentication
                authenticationMessage = Helper.getConcatPubKeys(publicGatewayKey, publicSHDKey);
                // authenticate by sending it to the SHD
                sendEncrypted(authenticationMessage, aprioriSharedKey, "CBC");
            }
            else {
                // wrong public keys used for authentication
                System.out.println("Gateway: SHD authentication failed!");
                System.out.println("Wrong public keys used for authentication!");
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
        try {
            publicSHDKey = receiveUnsecured();
        }
        catch (TimeoutException timeout)
        {
            System.out.println("Gateway: Timeout during receiving public keys!");
            commChannel.dropConnection();
            System.out.println("Gateway: Drop connection to that SHD!");
            return;
        }
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setThreadIndex(int threadIndex) {
        this.threadIndex = threadIndex;
    }
}
