package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

import sun.reflect.generics.reflectiveObjects.NotImplementedException;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public class SimpleChannel implements CommunicationChannel {

    @Override
    public void sendMessage(String dest, String string) {
        message = new String(string);
    }

    @Override
    public void sendMessage(String dest, byte[] message) {throw new NotImplementedException(); }

    @Override
    public String receiveMessage() {
        if (message == null)
            message = new String("");
        return message;
    }

    @Override
    public byte[] receiveByteArray() {
        throw new NotImplementedException();
    }

    public int getPort() {return 0;}

    private String message;
}
