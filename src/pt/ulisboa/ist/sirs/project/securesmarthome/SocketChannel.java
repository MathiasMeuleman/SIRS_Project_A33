package pt.ulisboa.ist.sirs.project.securesmarthome;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.SocketException;

/**
 * Created by Mathias on 2016-12-03.
 */
public abstract class SocketChannel {

    protected Socket socket;
    protected DataInputStream inStream;
    protected DataOutputStream outStream;

    public void sendMessage(byte[] message) throws SocketException {
        try {
            outStream.writeInt(message.length);
            outStream.write(message);
        } catch (SocketException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public byte[] receiveByteArray() throws SocketException {
        try {
            int size = inStream.readInt();
            byte[] message = new byte[size];
            inStream.readFully(message);
            return message;
        } catch(SocketException e) {
            throw e;
        } catch(IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void dropConnection() {
        try {
            socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}