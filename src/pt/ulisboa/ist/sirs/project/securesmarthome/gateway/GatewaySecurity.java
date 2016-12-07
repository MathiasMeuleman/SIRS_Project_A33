package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.SecurityManager;
import pt.ulisboa.ist.sirs.project.securesmarthome.AESSecretKeyFactory;
import pt.ulisboa.ist.sirs.project.securesmarthome.DHKeyAgreement;

import java.security.SecureRandom;
import java.util.Arrays;

/**
 * Created by Mathias on 2016-12-03.
 */
public class GatewaySecurity extends SecurityManager {

    private String key;
    private int SHD_ID;

    public GatewaySecurity(GatewaySocketChannel channel) {
        this.commChannel = channel;
    }

    @Override
    public void shareSessionKey() {
        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(key);
        // add the new input key to the list for authentication
        int SHD_ID = Gateway.aprioriSharedKeysList.size();
        Gateway.aprioriSharedKeysList.add(AESSecretKeyFactory.createSecretKey(key));

        // do DH to establish shared key
        receivePubKey();
        publicGatewayKey = DHKeyAgreement.getPublicGatewayKey(publicSHDKey);
        sendPubKey();
        DHKeyAgreement.createSharedSecretB(publicSHDKey);
        sessionKey = DHKeyAgreement.getSharedSecretKey();
        System.out.println("Finished DH");
    }

    @Override
    public void shareIV() {
        byte[] iv = null;
        while(iv == null) {
            iv = generateRandomIV();
        }
        sendEncrypted(iv, "ECB");
        Cryptography.setIV(iv);
    }

    @Override
    public void authenticate() {
        // receive authentication message from SHD
        byte[] authenticationMessage = receiveEncrypted(aprioriSharedKey, "CBC");
        if (authenticationMessage == null) {
            // wrong key!!!
            Gateway.smartHomeDevices.add(new AuthenticatedSHD(false));
            System.out.println("Gateway: SHD authentication failed!");
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
                System.out.println("Gateway: SHD authentication failed!");
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

    protected void sendPubKey() {
        sendUnsecured(publicGatewayKey);
    }

    protected void receivePubKey() {
        publicSHDKey = receiveUnsecured();
    }

    public void setKey(String key) {
        this.key = key;
    }
}
