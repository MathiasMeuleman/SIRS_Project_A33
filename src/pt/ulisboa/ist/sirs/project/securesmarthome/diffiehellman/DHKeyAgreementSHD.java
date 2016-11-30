package pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;

/**
 * Created by Mathias on 2016-11-30.
 */
public class DHKeyAgreementSHD implements DHKeyAgreement {
    @Override
    public void doDH(CommunicationChannel commChannel, byte[] pubKeyEncA, byte[] pubKeyEncB) {
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

    @Override
    public void authenticate() {

    }
}
