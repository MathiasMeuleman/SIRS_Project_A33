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
        while(true) {
            lightSim();
            security.resetConnection();
        }
    }

    private void lightSim()
    {
        while (true)
        {
            try {
                security.send((new String("Light status: ") + lightState.toString()).getBytes());
                byte[] command;
                command = security.receive();
                if(command != null) {
                    System.out.println("Command received: " + new String(command));
                    toggleLight();
                }
            } catch (SocketException e) {
                System.out.println("Connection was lost");
                return;
            }
        }
    }

    private void toggleLight()
    {
        if (lightState == LightState.ON)
            turnLightOff();
        else if (lightState == LightState.OFF)
            turnLightOn();
    }

    private void turnLightOff()
    {
        System.out.println("Turn light off");
        lightState = LightState.OFF;
    }

    private void turnLightOn()
    {
        System.out.println("Turn light on");
        lightState = LightState.ON;
    }

    private enum LightState{
        ON,OFF
    }
}
