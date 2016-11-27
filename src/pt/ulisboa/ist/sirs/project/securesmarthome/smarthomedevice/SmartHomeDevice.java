package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.Device;


/**
 * Created by Mathias on 2016-11-21.
 */
public class SmartHomeDevice extends Device{

    private CommunicationChannel channel;

    public SmartHomeDevice() {
        setCommunicationChannel();
    }

    public void setCommunicationChannel() {
        this.channel = new SocketChannel(CommunicationMode.SHD);
    }
}
