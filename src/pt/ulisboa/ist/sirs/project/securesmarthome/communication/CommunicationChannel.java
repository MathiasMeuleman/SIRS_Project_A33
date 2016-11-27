package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public interface CommunicationChannel {

    void sendMessage(String dest, String messages);
    void sendMessage(String dest, byte[] messages);

    String receiveMessage();
    byte[] receiveByteArray();

    int getPort();
}
