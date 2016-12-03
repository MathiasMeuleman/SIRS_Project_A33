package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;


import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.GatewaySocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.gateway.AuthenticatedSHD;
import pt.ulisboa.ist.sirs.project.securesmarthome.gateway.Gateway;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreementGateway;

import java.util.Arrays;

/**
 * Created by Mathias on 2016-12-03.
 */
public class GatewayPresentation extends PresentationClass {

    private int SHD_ID;
    private String key;

    public GatewayPresentation(GatewaySocketChannel channel) {
        this.commChannel = channel;
    }

    @Override
    public void shareSessionKey() {
        // add the new input key to the list for authentication
        SHD_ID = Gateway.aprioriSharedKeysList.size();
        Gateway.aprioriSharedKeysList.add(AESSecretKeyFactory.createSecretKey(key));

        // do DH to establish shared key
        DHKeyAgreement dh = new DHKeyAgreementGateway();
        receivePubKey();
        pubEncryptedGatewayKey = DHKeyAgreement2.getPubKeyEncGW(pubEncryptedSHDKey);
        sendPubKey();
        dh.doDH(pubEncryptedSHDKey);
        sessionKey = DHKeyAgreement2.getSharedSecretKey();
        System.out.println("Finished DH");
    }

    @Override
    public void authenticate() {
        // receive authentication message from SHD
        byte[] authenticationMessage = receiveWithoutTimestamp();
        if (authenticationMessage == null) {
            // wrong key!!!
            Gateway.smartHomeDevices.add(new AuthenticatedSHD(false));
            System.out.println("Gateway: SHD authentication failed!");
            commChannel.dropConnection();
            System.out.println("Drop connection to that SHD!");
        } else {
            // compare with concatenated keys
            byte[] concatKeys = Helper.getConcatPubKeys(pubEncryptedSHDKey, pubEncryptedGatewayKey);
            if (Arrays.equals(authenticationMessage, concatKeys)) {
                Gateway.smartHomeDevices.add(new AuthenticatedSHD(true));
                // authenticate gateway
                // generate authentication message
                authenticationMessage = Helper.getConcatPubKeys(pubEncryptedGatewayKey, pubEncryptedSHDKey);
                // authenticate by sending it to the other party
                sendWithoutTimestamp(authenticationMessage);

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

    protected void sendPubKey() {
        sendUnsecured(pubEncryptedGatewayKey);
    }

    protected void receivePubKey() {
        pubEncryptedSHDKey = receiveUnsecured();
    }

    public void setKey(String key) {
        this.key = key;
    }
}
