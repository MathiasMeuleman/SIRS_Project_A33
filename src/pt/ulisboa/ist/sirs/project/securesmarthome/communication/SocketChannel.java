package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Mathias on 2016-11-27.
 */
public class SocketChannel implements CommunicationChannel {

    private DataOutputStream clientSocket;
    private Socket serverSocket;

    public SocketChannel(boolean gateway) {
        if(gateway)
            initServerSocket(11000);
        else{
            initServerSocket(0);
        }
    }

    public void initServerSocket(int port) {
        try {
            ServerSocket servsock = new ServerSocket(port);
            serverSocket = servsock.accept();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendMessage(String dest, String message) {
        try {
            DataOutputStream stream = getClientSocket(dest);
            stream.writeBytes(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public String receiveMessage() {
        try {
            BufferedReader inputReader = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            return inputReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public DataOutputStream getClientSocket(String dest) throws IOException {
        if(clientSocket == null) {
            this.clientSocket = createSocket(dest);
        }
        return clientSocket;
    }

    public DataOutputStream createSocket(String dest) throws IOException {
        String[] address = dest.split(":");
        String IP = address[0];
        String port = address[1];
        Socket clientSocket = new Socket(IP, Integer.parseInt(port));
        return new DataOutputStream(clientSocket.getOutputStream());
    }
}
