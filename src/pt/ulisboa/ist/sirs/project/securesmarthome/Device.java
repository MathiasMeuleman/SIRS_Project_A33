package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SHDSocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.stationtostation.DHKeyAgreement;

import javax.crypto.SecretKey;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public class Device {

    public Device() {

    }

    public void run() {
        System.out.println("Running method in Device, should be Overridden in subclass");
    }

    public SecretKey getSessionKey() {
        return sessionKey;
    }

    protected SHDSocketChannel commChannel;
    protected byte[] pubEncryptedSHDKey;
    protected byte[] pubEncryptedGatewayKey;
    protected SecretKey sessionKey;
    protected SecretKey aprioriSharedKey;
    protected byte[] authenticationMessageEncrypted;
    protected DHKeyAgreement dh;
    protected byte[] authenticationMessage;
}
