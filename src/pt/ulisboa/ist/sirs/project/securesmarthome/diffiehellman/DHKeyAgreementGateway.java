package pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman;

/**
 * Created by Mathias on 2016-11-30.
 */
public class DHKeyAgreementGateway implements DHKeyAgreement {

    @Override
    public void doDH(byte[] pubKeyEncA) {
        try {
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
