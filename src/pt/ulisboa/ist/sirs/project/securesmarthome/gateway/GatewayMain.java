package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

/**
 * Created by Mathias on 2016-12-04.
 */
public class GatewayMain {

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            throw new Exception("Wrong number of command options");
        }
        System.out.println("Initializing gateway");
        Gateway gateway = new Gateway(args[0]);
        gateway.run();
    }
}
