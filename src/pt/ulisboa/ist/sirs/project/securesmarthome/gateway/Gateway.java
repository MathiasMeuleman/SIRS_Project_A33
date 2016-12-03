package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by Mathias on 2016-11-21.
 */
public class Gateway {

    public static List<SecretKey> aprioriSharedKeysList;
    public static List<AuthenticatedSHD> smartHomeDevices;
    private String key;
    private ServerSocket serverSocket;

    public Gateway(String key) {
        smartHomeDevices = new ArrayList<>();
        aprioriSharedKeysList = new ArrayList<>();
        this.key = key;
        try {
            serverSocket = new ServerSocket(11000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while (true) {
            // Wait for notification from webserver that the user wants to add a new SHD and then open serversocket, connect and execute
//            waitForUserRequestAddShd()
            try {
                System.out.println("Waiting for client");
                Socket socket = serverSocket.accept();
                GatewaySocketChannel channel = new GatewaySocketChannel(socket);
                // Should get a timeout
                System.out.println("Accepted client, creating thread");
                GatewayThread thread = new GatewayThread(new GatewaySecurity(channel), key);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
