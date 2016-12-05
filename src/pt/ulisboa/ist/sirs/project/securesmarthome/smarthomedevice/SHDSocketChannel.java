package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.SocketChannel;

import java.io.*;
import java.net.Socket;

/**
 * Created by Mathias on 2016-11-27.
 */
public class SHDSocketChannel extends SocketChannel {

    private String address = "192.168.43.212";
//    private String address = "localhost";
    private Socket clientSocket;

    public SHDSocketChannel() {
        try {
            clientSocket = new Socket(address, 11000);
            outStream = new DataOutputStream(clientSocket.getOutputStream());
            inStream = new DataInputStream(clientSocket.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
