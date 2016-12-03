package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.SecurityManager;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement;

import java.util.Arrays;

/**
 * Created by Mathias on 2016-12-03.
 */
public class SHDSecurity extends SecurityManager {

    public SHDSecurity() {
        this.commChannel = new SHDSocketChannel();
    }

    @Override
    public void shareSessionKey() {
        pubEncryptedSHDKey = DHKeyAgreement.getPubKeyEncSHD("-gen");
        sendPubKey();
        receivePubKey();
        DHKeyAgreement.createSharedSecretA(pubEncryptedGatewayKey);
        sessionKey = DHKeyAgreement.getSharedSecretKey();
        System.out.println("Finished DH");
    }

    @Override
    public void authenticate()
    {
        // generate authentication message
        authenticationMessage = Helper.getConcatPubKeys(pubEncryptedSHDKey, pubEncryptedGatewayKey);
        // authenticate by sending it to the other party
        sendWithoutTimestamp(authenticationMessage);
        // receive authentication message from Gateway
        authenticationMessage = receiveWithoutTimestamp();
        if (authenticationMessage == null)
        {
            // wrong key!!!
            System.out.println("SHD: Gateway authentication failed!");
        }
        else {
            // compare with concatenated keys
            byte[] concatKeys = Helper.getConcatPubKeys(pubEncryptedGatewayKey, pubEncryptedSHDKey);
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

    protected void sendPubKey() {
        sendUnsecured(pubEncryptedSHDKey);
    }

    protected void receivePubKey() {
        pubEncryptedGatewayKey = receiveUnsecured();
    }
}
