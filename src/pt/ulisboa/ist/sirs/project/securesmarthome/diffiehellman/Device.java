package pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;

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
        keyAgree = new DHKeyAgreement2("-gen", commMode, commChannel, "localhost:11000");
        sharedSecret = keyAgree.getSharedSecret();
    }

    protected CommunicationChannel commChannel;
    private DHKeyAgreement2 keyAgree;
    private byte[] sharedSecret;
}
