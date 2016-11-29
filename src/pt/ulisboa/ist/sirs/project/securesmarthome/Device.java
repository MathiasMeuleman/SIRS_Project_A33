package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.Authentication;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreement2;
import javax.crypto.SecretKey;

import static pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHStatus.PUBKEYEXCHANGED;
import static pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHStatus.SHAREDKEYGENERATED;
import static pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHStatus.START;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public class Device {

    public Device(CommunicationMode commMode)
    {
        commChannel = new SocketChannel(commMode);

        // doing DH key exchange
        keyAgree = new DHKeyAgreement2("-gen", commMode, commChannel, "localhost:11000");
        dhSharedSecretKey = keyAgree.getSharedSecretKey();
    }

    protected CommunicationChannel commChannel;
    private DHKeyAgreement2 keyAgree;
    protected byte[] pubKeyEncA;
    protected byte[] pubKeyEncB;
    protected SecretKey dhSharedSecretKey;
    protected SecretKey aprioriSharedKey;
    protected byte[] authenticationMessageEncrypted;
}
