package pt.ulisboa.ist.sirs.project.securesmarthome.communication;

/**
 * Created by Alex Anders on 21/11/2016.
 */
public class SimpleChannel implements CommunicationChannel {

    @Override
    public void sendMessage(String dest, String string) {
        message = new String(string);
    }

    @Override
    public String receiveMessage() {
        if (message == null)
            message = new String("");
        return message;
    }

    private String message;
}
