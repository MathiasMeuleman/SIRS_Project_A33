package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import java.net.SocketException;

/**
 * Created by Mathias on 2016-12-03.
 */
public class GatewayThread extends Thread {

    private GatewaySecurity security;

    public GatewayThread(int index, GatewaySecurity security) {
        this.security = security;
        this.security.setThreadIndex(index);
        Gateway.threadIDs.add(this);
        this.security.shareUUID();
    }

    @Override
    public void run() {
        security.connectToDevice();
        while(true) {
            try {
                collectData();
                sendCommands();
            } catch (SocketException e) {
                System.out.println("Connection lost with SHD");
                break;
            }
        }
    }

    public void collectData() throws SocketException {
            byte[] data;
            data = security.receive();
            if(data != null)
                System.out.println("Received: " + new String(data));
    }

    public void sendCommands() throws SocketException {
        // toggle light bulb
        String commandString = new String("Toggle Light");
        byte[] command;
        command = commandString.getBytes();
        System.out.println("Sending command: " + commandString);
        security.send(command);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void setKey(String key) {
        this.security.setKey(key);
    }
}
