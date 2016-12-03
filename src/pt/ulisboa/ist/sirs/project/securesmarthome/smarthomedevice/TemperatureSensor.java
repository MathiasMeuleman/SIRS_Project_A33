package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

/**
 * Created by Mathias on 2016-12-03.
 */
public class TemperatureSensor {

    private PresentationClass shd;

    public TemperatureSensor(PresentationClass shd) {
        this.shd = shd;
    }

    public void run() {
        temperatureSim();
    }

    private void temperatureSim() {
        //Simulate a temperature device :)
        int temp = 10;
        for (int i = 0; i <= 12; i++) {
            String data = "" + temp;
            byte[] dataBytes = data.getBytes();
            shd.send(dataBytes);
            temp++;
        }
        for (int i = 0; i <= 12; i++) {
            String data = "" + temp;
            byte[] dataBytes = data.getBytes();
            shd.send(dataBytes);
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
