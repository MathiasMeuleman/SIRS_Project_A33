package pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;

import static pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHStatus.PUBKEYEXCHANGED;
import static pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHStatus.SHAREDKEYGENERATED;
import static pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHStatus.START;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public class Device {

    public Device(DHRole dhRole)
    {
        keyAgree = new DHKeyAgreement2();
        role = dhRole;
        status = DHStatus.START;
    }

    public void dhKeyAgreement(String argv, CommunicationChannel commChannelObject, String ipPortDest) {

        switch (status) {
            case START: {
                try {
                    String mode = "USE_SKIP_DH_PARAMS";

                    if (argv != null) {
                        if (!(argv.equals("-gen"))) {
                            keyAgree.usage();
                            throw new Exception("Unrecognized flag: " + argv);
                        }
                        mode = "GENERATE_DH_PARAMS";
                    }

                    // if the device is Alice
                    if (role == DHRole.ALICE) {
                        // generating pubKeyEncAlice
                        byte[] pubKeyEnc = keyAgree.getPubKeyEncAlice(mode);
                        // sending the pubKeyEnc to Bob
                        commChannelObject.sendMessage(ipPortDest, pubKeyEnc);
                        // change state
                        status = PUBKEYEXCHANGED;
                    }

                    // if the device is Bob
                    if (role == DHRole.BOB) {
                        // receiving the pubKeyEnc of Alice
                        byte[] pubKeyEncAlice = commChannelObject.receiveMessage().getBytes();
                        // generating pubKeyEncBob
                        byte[] pubKeyEncBob = keyAgree.getPubKeyEncBob(pubKeyEncAlice);
                        // sending pubKeyEncBob to Alice
                        commChannelObject.sendMessage(ipPortDest, pubKeyEncBob);
                        // create shared secret
                        keyAgree.createSharedSecretBob();
                        // change state
                        status = SHAREDKEYGENERATED;
                    }
                }
                catch (Exception e) {
                    System.err.println("Error: " + e);
                    System.exit(1);
                }
            }
            case PUBKEYEXCHANGED: {
                try {
                    if (role == DHRole.ALICE)
                    {
                        // receiving the pubKeyEnc of Bob
                        byte [] pubKeyEncBob = commChannelObject.receiveMessage().getBytes();
                        // create shared secret
                        keyAgree.createSharedSecretAlice(pubKeyEncBob);
                    }
                }
                catch (Exception e) {
                    System.err.println("Error: " + e);
                    System.exit(1);
                }
                // change state
                status = SHAREDKEYGENERATED;
            }
            case SHAREDKEYGENERATED: {
                try {
                    sharedSecret = keyAgree.getSharedSecret(role);
                    if (sharedSecret == null)
                        System.out.println("Failure! DHRole is invalid");
                }
                catch (Exception e) {
                    System.err.println("Error: " + e);
                    System.exit(1);
                }
            }
        }
    }

    private DHKeyAgreement2 keyAgree;
    private DHRole role;
    private DHStatus status;
    private byte[] sharedSecret;
}
