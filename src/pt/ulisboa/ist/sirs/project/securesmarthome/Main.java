package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.gateway.Gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice.SmartHomeDevice;

/**
 * Created by Alex Anders on 18/11/2016.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        if (args.length < 1) {
            throw new Exception("Wrong number of command options");
        }

        if (args[0].equals("gateway")) {
            if (args.length != 2) {
                throw new Exception("Wrong number of command options");
            }
            System.out.println("Initializing gateway");
            Gateway gateway = new Gateway(CommunicationMode.GATEWAY, args[1].getBytes());
            System.out.println("Testing");
        }

        if (args[0].equals("smartHomeDevice")) {
            if (args.length != 1) {
                throw new Exception("Wrong number of command options");
            }
            System.out.println("Initializing SHD");
            SmartHomeDevice smartHomeDevice = new SmartHomeDevice(CommunicationMode.SHD);
        }
    }
}