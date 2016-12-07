package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;
import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;


/**
 * Created by Mathias on 2016-11-21.
 */
public class Gateway {

    public static List<GatewayThread> threadIDs;
    public static Map<UUID, String> aprioriSharedKeysList;
    public static Map<UUID, GatewayThread> threadList;
    public static List<AuthenticatedSHD> smartHomeDevices;
    public static UUID newConnectionUUID;

    private long timeRef;
    private String key;
    private ServerSocket serverSocket;

    public Gateway(String key) {
        threadIDs = new ArrayList<>();
        smartHomeDevices = new ArrayList<>();
        aprioriSharedKeysList = new HashMap<>();
        threadList = new HashMap<>();
        this.key = key;
        timeRef = Helper.initTimestamp();
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
                int index = threadIDs.size();
                GatewayThread thread = new GatewayThread(index, new GatewaySecurity(channel, 0));
                String apriori = aprioriSharedKeysList.get(newConnectionUUID);
                if(apriori == null) {
                    if(key == null) {
                        System.out.println("No key supplied, bitch!");
                    } else {
                        thread.setKey(key);
                    }
                } else {
                    thread.setKey(apriori);
                }
                thread.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void linkThreadToUUID(int threadIndex, UUID uuid) {
        threadList.put(uuid, threadIDs.get(threadIndex));
    }
}
