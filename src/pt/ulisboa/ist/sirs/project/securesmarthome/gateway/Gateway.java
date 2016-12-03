package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.GatewaySocketChannel;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by Mathias on 2016-11-21.
 */
public class Gateway extends Device {

    public static Map<Integer, SecretKey> aprioriSharedKeysList;
    public static List<AuthenticatedSHD> smartHomeDevices;
    private String key;
    private ServerSocket serverSocket;

    public Gateway(String key) {
        super();
        smartHomeDevices = new ArrayList<>();
        aprioriSharedKeysList = new HashMap<>();
        this.key = key;
        try {
            serverSocket = new ServerSocket(11000);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
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
                GatewayThread thread = new GatewayThread(channel, key);
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
