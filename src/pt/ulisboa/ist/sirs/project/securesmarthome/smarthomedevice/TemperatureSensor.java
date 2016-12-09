package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import java.net.SocketException;

/**
 * Created by Mathias on 2016-12-03.
 */
public class TemperatureSensor {

    private SHDSecurity security;

    public TemperatureSensor(SHDSecurity security) {
        this.security = security;
    }

    public void run() {
        security.connectToDevice();
        System.out.println("Ready");
        while(true) {
            temperatureSim();
            System.out.println("Reconnecting");
            security.resetConnection();
        }
    }

    private void temperatureSim() {
        while(true) {
            //Simulate a temperature device :)
            int temp = 10;
            for (int i = 0; i <= 12; i++) {
                String data = "Temperature sensor " + temp + "°C";
                byte[] dataBytes = data.getBytes();
                try {
                    security.send(dataBytes);
                } catch (SocketException e) {
                    System.out.println("Connection was lost");
                    return;
                }
                temp++;
            }
            for (int i = 0; i <= 12; i++) {
                String data = "Temperature sensor " + temp + "°C";
                byte[] dataBytes = data.getBytes();
                try {
                    security.send(dataBytes);
                } catch (SocketException e) {
                    e.printStackTrace();
                }
                temp--;
                try {
                    Thread.sleep(300);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
