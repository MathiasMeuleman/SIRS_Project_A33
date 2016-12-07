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
        System.out.println("Thread started " + this.getId());
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
        while(true) {
            byte[] data;
            data = security.receive();
            System.out.println("Received: " + new String(data));
        }
    }

    public void sendCommands() {
//        if(hasValidCommand) {
//            send();
//        }
    }

    public void setKey(String key) {
        this.security.setKey(key);
    }
}
