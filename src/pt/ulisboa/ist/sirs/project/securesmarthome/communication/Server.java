package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

/**
 * Created by Maxwell on 11/27/2016.
 *
 * Server class must run individual from the client
 * Run 'Server.main()', then run 'Client.main()'
 * In our case the server could be the SHD that is listening to TCP port 6789
 */

import java.io.*;
import java.net.*;

public class Server {

    public static void main(String argv[]) throws Exception
    {
        String gatewayMessage;
        ServerSocket welcomeSocket = new ServerSocket(6789);

        while(true)
        {
            Socket connectionSocket = welcomeSocket.accept();
            System.out.println("Waiting for connection...");
            BufferedReader inFromClient = new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));
            gatewayMessage = inFromClient.readLine();

            System.out.println("Connected with " + gatewayMessage);
            //To be implemented...
            System.out.println("if authentication == true {Diffie-Hellman, Session key...}");
            System.out.println("else {Drop connection}");

        }
    }
}
