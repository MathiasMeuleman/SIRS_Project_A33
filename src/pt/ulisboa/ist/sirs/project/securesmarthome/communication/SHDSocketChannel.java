package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Arrays;

/**
 * Created by Mathias on 2016-11-27.
 */
public class SHDSocketChannel {

    private Socket clientSocket;
    private DataInputStream inStream;
    private DataOutputStream outStream;

    public SHDSocketChannel() {
        try {
            clientSocket = new Socket("localhost", 11000);
            outStream = new DataOutputStream(clientSocket.getOutputStream());
            inStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
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
}
