package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;


/**
 * Created by Mathias on 2016-11-21.
 */
public class SmartHomeDevice extends Device{

    public SmartHomeDevice(CommunicationMode commMode) {
        super(commMode);
//        setCommunicationChannel();
    }

    public void setCommunicationChannel() {
        commChannel = new SocketChannel(CommunicationMode.SHD);
    }
}
