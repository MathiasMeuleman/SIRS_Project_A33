package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public interface CommunicationChannel {

    void sendMessage(byte[] bytes);

    String receiveMessage();
}
