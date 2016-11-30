package pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;

/**
 * Created by Mathias on 2016-11-30.
 */
public class DHKeyAgreementSHD implements DHKeyAgreement {

    @Override
    public void doDH(byte[] pubKeyEncB) {
        // create shared secret
        try {
            DHKeyAgreement2.createSharedSecretA(pubKeyEncB);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void authenticate() {

    }
}
