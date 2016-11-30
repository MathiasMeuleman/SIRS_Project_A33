package pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;

/**
 * Created by Mathias on 2016-11-30.
 */
public interface DHKeyAgreement {

    void doDH(CommunicationChannel commChannel, byte[] pubKeyEncA, byte[] pubKeyEncB);
    void authenticate();
}