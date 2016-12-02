package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Mathias on 2016-11-27.
 */
public class SocketChannel implements CommunicationChannel {

    private int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataInputStream inStream;
    private DataOutputStream outStream;
    private CommunicationMode mode;

    public SocketChannel(CommunicationMode mode) {
        this.mode = mode;
        setup();
    }

    public void setup() {
        try {
            switch (mode) {
                case GATEWAY:
                    setupGateway();
                    break;
                case SHD:
                    setupSmartHomeDevice();
                    break;
                case USER:
                    setupUser();
                    break;
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void setupGateway() throws IOException {
        port = 11000;
        serverSocket = new ServerSocket(port);
        clientSocket = serverSocket.accept();
        outStream = new DataOutputStream(clientSocket.getOutputStream());
        inStream = new DataInputStream(clientSocket.getInputStream());
    }

    private void setupSmartHomeDevice() throws IOException {
        port = 12005;
        try {
            clientSocket = new Socket("localhost", 11000);
            outStream = new DataOutputStream(clientSocket.getOutputStream());
            inStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void gatewayDropConnection() {
        try {
            serverSocket.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupUser() {

    }

    public void sendMessage(String dest, byte[] message) {
        try {
            outStream.writeInt(message.length);
            outStream.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receiveByteArray() {
        try {
            int size = inStream.readInt();
            byte[] message = new byte[size];
            inStream.readFully(message);
            return message;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getPort() {
        return port;
    }
}
