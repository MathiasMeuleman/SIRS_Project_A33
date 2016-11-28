package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHRole;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.Device;


/**
 * Created by Mathias on 2016-11-21.
 */
public class Gateway extends Device{

    private CommunicationChannel channel;

    public Gateway(DHRole dhRole) {
        super(dhRole);
        setCommunicationChannel();
    }

    public void setCommunicationChannel() {
        channel = new SocketChannel(CommunicationMode.GATEWAY);
    }
}
