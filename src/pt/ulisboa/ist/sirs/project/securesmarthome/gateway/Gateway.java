package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.Authentication;


/**
 * Created by Mathias on 2016-11-21.
 */
public class Gateway extends Device{

    public Gateway(CommunicationMode commMode, byte[] key) {
        super(commMode);
        aprioriSharedKey = key;

        // authentication part
        authenticationMessageEncrypted = Authentication.getConcatPubKeyBAEncrypted(
                aprioriSharedKey, pubKeyEncA, pubKeyEncB);
    }

    public void setCommunicationChannel() {
        commChannel = new SocketChannel(CommunicationMode.GATEWAY);
    }
}
