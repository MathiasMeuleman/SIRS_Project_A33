package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHRole;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.Device;

/**
 * Created by Mathias on 2016-11-21.
 */
public class SmartHomeDevice extends Device{
    public SmartHomeDevice(CommunicationChannel commChannel, DHRole dhRole) {
        super(commChannel, dhRole);
    }
}
