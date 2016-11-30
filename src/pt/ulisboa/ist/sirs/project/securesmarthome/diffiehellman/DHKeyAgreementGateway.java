package pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;

/**
 * Created by Mathias on 2016-11-30.
 */
public class DHKeyAgreementGateway implements DHKeyAgreement {

    @Override
    public void doDH(CommunicationChannel commChannel, byte[] pubKeyEncA, byte[] pubKeyEncB) {
        try {
            // receive the pubKey from the other party
            pubKeyEncA = commChannel.receiveByteArray();
            pubKeyEncB = DHKeyAgreement2.getPubKeyEncB(pubKeyEncA);
            // sending pubKey to other party
            String dest = new String("localhost:" + commChannel.getPort());
            commChannel.sendMessage(dest, pubKeyEncB);
            // create shared secret
            DHKeyAgreement2.createSharedSecretB(pubKeyEncA);
        } catch (Exception e) {
            System.err.println("Error: " + e);
            System.exit(1);
        }
    }

    @Override
    public void authenticate() {

    }
}
