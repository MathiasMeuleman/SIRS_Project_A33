package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreement;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreement2;

import javax.crypto.SecretKey;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public class Device {

    public Device(CommunicationMode commMode) {

        // setup channel
        commChannel = new SocketChannel(commMode);
    }

    public void setPubEncryptedSHDKey(byte[] pubEncryptedSHDKey) {
        this.pubEncryptedSHDKey = pubEncryptedSHDKey;
    }

    public void setPubEncryptedGatewayKey(byte[] pubEncryptedGatewayKey) {
        this.pubEncryptedGatewayKey = pubEncryptedGatewayKey;
    }

    protected CommunicationChannel commChannel;
    protected byte[] pubEncryptedSHDKey;
    protected byte[] pubEncryptedGatewayKey;
    protected SecretKey dhSharedSecretKey;
    protected SecretKey aprioriSharedKey;
    protected byte[] authenticationMessageEncrypted;
    protected DHKeyAgreement dh;
    protected byte[] authenticationMessage;
}
