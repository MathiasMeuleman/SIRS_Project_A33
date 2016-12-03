package pt.ulisboa.ist.sirs.project.securesmarthome;
import pt.ulisboa.ist.sirs.project.securesmarthome.gateway.Gateway;
import pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice.GatewayPresentation;
import pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice.PresentationClass;
import pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice.SHDPresentation;
import pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice.TemperatureSensor;


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
            Gateway gateway = new Gateway(args[1]);
            gateway.run();
        }

        if (args[0].equals("smartHomeDevice")) {
            if (args.length != 1) {
                throw new Exception("Wrong number of command options");
            }
            System.out.println("Initializing SHD");
            TemperatureSensor sens = new TemperatureSensor(new SHDPresentation());
            sens.run();
        }
    }
}