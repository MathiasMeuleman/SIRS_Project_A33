package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreement;

import javax.crypto.SecretKey;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public class Device implements Runnable {

    public Device(CommunicationMode commMode) {

        // setup channel
        commChannel = new SocketChannel(commMode);
    }

    public void run() {
        System.out.println("Running method in Device, should be Overridden in subclass");
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
