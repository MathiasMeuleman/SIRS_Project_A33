package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

/**
 * Created by Maxwell on 11/27/2016.
 *
 * Client class must run after the server class is running
 * The client will try to initiate TCP connection on port 6789 where the server listening
 * In our case the client would be the gateway trying to connect to the SHD
 *
 */

import java.io.*;
import java.net.*;

public class Client {
    public static void main(String argv[]) throws Exception
    {
        Socket clientSocket = new Socket("localhost", 6789);
        DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

        outToServer.writeBytes("Gateway(192.168.2.254)" + '\n');
        //To be implemented...
        System.out.println("if authentication == true {Diffie-Hellman, Session key...}");
        System.out.println("else {Drop connection}");
        clientSocket.close();
    }
}
