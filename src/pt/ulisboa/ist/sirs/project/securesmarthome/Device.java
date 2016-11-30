package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreement2;

import javax.crypto.SecretKey;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public class Device {

    public Device(CommunicationMode commMode) {

        // setup channel
        commChannel = new SocketChannel(commMode);

        // setup secret key with DH
        dhKeyAgreement(commMode);

    }

    public void dhKeyAgreement(CommunicationMode commMode)
    {
        // doing DH key exchange
        if (commMode == CommunicationMode.SHD) {
            try {
                pubKeyEncA = DHKeyAgreement2.getPubKeyEncA("-gen");
                // send the pubKey to the other party
                commChannel.sendMessage("localhost:11000", pubKeyEncA);
                // receiving the pubKeyEnc of other party
                pubKeyEncB = commChannel.receiveByteArray();
                // create shared secret
                DHKeyAgreement2.createSharedSecretA(pubKeyEncB);
            } catch (Exception e) {
                System.err.println("Error: " + e);
                System.exit(1);
            }
        }

        if (commMode == CommunicationMode.GATEWAY) {
            try {
                // receive the pubKey from the other party
                pubKeyEncA = commChannel.receiveByteArray();
                pubKeyEncB = DHKeyAgreement2.getPubKeyEncB(pubKeyEncA);
                // sending pubKey to other party
                commChannel.sendMessage(pubKeyEncB);
                // create shared secret
                DHKeyAgreement2.createSharedSecretB(pubKeyEncA);
            } catch (Exception e) {
                System.err.println("Error: " + e);
                System.exit(1);
            }
        }

        dhSharedSecretKey = DHKeyAgreement2.getSharedSecretKey();
    }

    protected CommunicationChannel commChannel;
    protected byte[] pubKeyEncA;
    protected byte[] pubKeyEncB;
    protected SecretKey dhSharedSecretKey;
    protected SecretKey aprioriSharedKey;
    protected byte[] authenticationMessageEncrypted;
    protected byte[] authenticationMessage;
}
