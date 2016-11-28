package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

import com.sun.corba.se.spi.orbutil.fsm.InputImpl;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.Buffer;

/**
 * Created by Mathias on 2016-11-27.
 */
public class SocketChannel implements CommunicationChannel {

    private int port;
    private ServerSocket serverSocket;
    private Socket clientSocket;
    private DataOutputStream outStream;
    private CommunicationMode mode;

    public SocketChannel(CommunicationMode mode) {
        this.mode = mode;
        setup();
    }

    private void setup() {
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
    }

    private void setupSmartHomeDevice() throws IOException {
        serverSocket = new ServerSocket(0);
        port = serverSocket.getLocalPort();
    }

    private void setupUser() {

    }

    @Override
    public void sendMessage(String dest, String message) {
        try {
            getSocket(dest);
            outStream = new DataOutputStream(clientSocket.getOutputStream());
//            outStream.writeChar('s');
            outStream.writeInt(message.length());
            outStream.writeBytes(message);
            outStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String dest, byte[] message) {
        try {
            getSocket(dest);
            outStream = new DataOutputStream((clientSocket.getOutputStream()));
//            outStream.writeChar('b');
            outStream.writeInt(message.length);
            outStream.write(message);
            outStream.close();
            clientSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void getSocket(String dest) throws IOException {
        String[] address = dest.split(":");
        String IP = address[0];
        int destPort = Integer.parseInt(address[1]);
        clientSocket = new Socket(IP, destPort);
    }

    @Override
    public String receiveMessage() {
        return receiveString();
    }

    public String receiveString() {
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from client");
            BufferedReader inStream = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
//            if (inStream.read() != 's') {
//                System.err.println("Message not of type String");
//                return null;
//            }
            // Discard size param
            inStream.read();

            StringBuilder builder = new StringBuilder();
            String m;
            while ((m = inStream.readLine()) != null) {
                builder.append(m);
            }
            String message = builder.toString();
            inStream.close();
            clientSocket.close();
            return message;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] receiveByteArray() {
        try {
            clientSocket = serverSocket.accept();
            System.out.println("Accepted connection from client");
            DataInputStream inStream = new DataInputStream(clientSocket.getInputStream());
//            if(inStream.read() != 'b') {
//                System.err.println("Message not of type byte[]");
//                return null;
//            }
            int size = inStream.read();
            byte[] message = new byte[size];
            inStream.readFully(message);
            inStream.close();
            clientSocket.close();
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
