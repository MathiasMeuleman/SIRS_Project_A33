package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

/**
 * Created by Mathias on 2016-12-03.
 */
public class GatewayThread extends Thread {

    private GatewaySecurity presentation;

    public GatewayThread(GatewaySecurity presentation, String key) {
        this.presentation = presentation;
        this.presentation.setKey(key);
    }

    @Override
    public void run() {
        presentation.connectToDevice();
        tempSim();
    }

    public void tempSim() {
        while(true) {
            byte[] data = presentation.receive();
            System.out.println("Received: " + new String(data));
        }
    }
}
