package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice.GatewayPresentation;

import java.util.Arrays;

/**
 * Created by Mathias on 2016-12-03.
 */
public class GatewayThread extends Thread {

    private GatewayPresentation presentation;

    public GatewayThread(GatewayPresentation presentation, String key) {
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
