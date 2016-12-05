package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import java.net.SocketException;

/**
 * Created by Mathias on 2016-12-03.
 */
public class LightBulb {

    private SHDSecurity security;
    private LightState lightState;

    public LightBulb(SHDSecurity security) {
        this.security = security;
        lightState = LightState.OFF;
    }

    public void run() {
        security.connectToDevice();
        lightSim();
    }

    private void lightSim()
    {
        while (true)
        {
            try {
                String command = security.receive().toString();
                System.out.println("command received: " + command);
                toggleLight();
                // acknowledge the process
                security.send(lightState.toString().getBytes());
            } catch (SocketException e) {
                e.printStackTrace();
            }
        }
    }

    private void toggleLight()
    {
        if (lightState == LightState.ON)
            turnLightOff();
        if (lightState == LightState.OFF)
            turnLightOn();
    }

    private void turnLightOff()
    {
        lightState = LightState.OFF;
    }

    private void turnLightOn()
    {
        lightState = LightState.ON;
    }

    private enum LightState{
        ON,OFF
    }
}
