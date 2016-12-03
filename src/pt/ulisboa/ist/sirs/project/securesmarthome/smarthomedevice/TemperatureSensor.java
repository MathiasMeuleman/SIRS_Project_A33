package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

/**
 * Created by Mathias on 2016-12-03.
 */
public class TemperatureSensor {

    private SHDSecurity presentation;

    public TemperatureSensor(SHDSecurity presentation) {
        this.presentation = presentation;
    }

    public void run() {
        presentation.connectToDevice();
        temperatureSim();
    }

    private void temperatureSim() {
        //Simulate a temperature device :)
        int temp = 10;
        for (int i = 0; i <= 12; i++) {
            String data = "" + temp;
            byte[] dataBytes = data.getBytes();
            presentation.send(dataBytes);
            temp++;
        }
        for (int i = 0; i <= 12; i++) {
            String data = "" + temp;
            byte[] dataBytes = data.getBytes();
            presentation.send(dataBytes);
            temp--;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        while(true);
    }
}
