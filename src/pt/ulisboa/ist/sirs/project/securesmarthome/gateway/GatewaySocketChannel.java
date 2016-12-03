package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.SocketChannel;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;

/**
 * Created by Mathias on 2016-12-03.
 */
public class GatewaySocketChannel extends SocketChannel {

    public GatewaySocketChannel(Socket socket) {
        this.socket = socket;
        try {
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

//    public void sendMessage(byte[] message) {
//        try {
//            outStream.writeInt(message.length);
//            outStream.write(message);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//    public byte[] receiveByteArray() {
//        try {
//            int size = inStream.readInt();
//            byte[] message = new byte[size];
//            inStream.readFully(message);
//            return message;
//        } catch(IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }
}
