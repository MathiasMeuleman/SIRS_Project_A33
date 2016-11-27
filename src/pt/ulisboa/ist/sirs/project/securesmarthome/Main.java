package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SimpleChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.gateway.Gateway;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESKeyGenerator;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.GenericKeyStore;

import javax.crypto.SecretKey;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice.SmartHomeDevice;

/**
 * Created by Alex Anders on 18/11/2016.
 */
public class Main {
    public static void main(String[] args) throws Exception {

        if ((args.length > 2) || (args.length < 1)) {
            throw new Exception("Wrong number of command options");
        }

        if (args[0].equals("gateway")) {
            System.out.println("Initializing gateway");
            Gateway gateway = new Gateway();
            System.out.println("Testing");
//            gateway.dhKeyAgreement(args[1], simpleChannel);
        }
        if (args[0].equals("smartHomeDevice")) {
            System.out.println("Initializing SHD");
            SmartHomeDevice smartHomeDevice = new SmartHomeDevice();
//            smartHomeDevice.dhKeyAgreement(args[1], simpleChannel);
        }
    }
}