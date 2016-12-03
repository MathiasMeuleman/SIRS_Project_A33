package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SHDSocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreementSHD;

import java.util.Arrays;

/**
 * Created by Mathias on 2016-12-03.
 */
public class SHDPresentation extends PresentationClass {

    public SHDPresentation() {
        this.commChannel = new SHDSocketChannel();
    }

    @Override
    public void shareSessionKey() {
        dh = new DHKeyAgreementSHD();
        pubEncryptedSHDKey = DHKeyAgreement2.getPubKeyEncSHD("-gen");
        sendPubKey();
        receivePubKey();
        dh.doDH(pubEncryptedGatewayKey);
        sessionKey = DHKeyAgreement2.getSharedSecretKey();
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
