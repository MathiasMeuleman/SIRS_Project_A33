package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;

import javax.crypto.SecretKey;
import java.time.Instant;


/**
 * Created by Mathias on 2016-11-21.
 */
public abstract class SecurityManager {

    private static final long TIMESTAMP_THRESHOLD = Long.MAX_VALUE;

    private Instant timestampReference;
    protected SocketChannel commChannel;
    protected byte[] pubEncryptedSHDKey;
    protected byte[] pubEncryptedGatewayKey;
    protected SecretKey sessionKey;
    protected SecretKey aprioriSharedKey;
    protected byte[] authenticationMessage;

    public SecurityManager() {
        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(printedKey);
    }

    public void connectToDevice() {
        // Station-to-Station
        shareSessionKey();
        authenticate();
        makeRefTime();
    }

    public abstract void shareSessionKey();
    public abstract void authenticate();

    public void makeRefTime() {
        long reftime = Helper.bytesToLong(sessionKey.getEncoded());
        timestampReference = Instant.ofEpochMilli(reftime);
    }

    public void send(byte[] data) {
        byte[] toSend = addTimestamp(data);
        byte[] encrypted = Cryptography.encrypt(toSend, sessionKey);
        commChannel.sendMessage(encrypted);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendWithoutTimestamp(byte[] data) {
        byte[] encrypted = Cryptography.encrypt(data, sessionKey);
        commChannel.sendMessage(encrypted);
        try {
            Thread.sleep(300);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void sendUnsecured(byte[] data) {
        commChannel.sendMessage(data);
    }

    public byte[] receive() {
        byte[] encrypted = commChannel.receiveByteArray();
        byte[] received = Cryptography.decrypt(encrypted, sessionKey);
        long timestamp = retrieveTimestamp(received);
        if(checkTimestamp(timestamp)) {
            return retrieveData(received);
        }
        return null;
    }

    public byte[] receiveWithoutTimestamp() {
        byte[] encrypted = commChannel.receiveByteArray();
        byte[] received = Cryptography.decrypt(encrypted, sessionKey);
        return received;
    }

    public byte[] receiveUnsecured() {
        byte[] data = commChannel.receiveByteArray();
        return data;
    }

    /**
     * Prepend current timestamp to the data
     * @param data
     * @return
     */
    public byte[] addTimestamp(byte[] data) {
        Instant inst = Instant.now();
        long timestamp = inst.toEpochMilli() - timestampReference.toEpochMilli();
        byte[] stampBytes = Helper.longToBytes(timestamp);
        int size = data.length + stampBytes.length;
        byte[] toSend = new byte[size];
        for (int i = 0; i < stampBytes.length; i++) {
            toSend[i] = stampBytes[i];
        }
        for (int i = 0; i < data.length; i++) {
            toSend[stampBytes.length + i] = data[i];
        }
        return toSend;
    }

    private byte[] retrieveData(byte[] received) {
        byte[] data = new byte[received.length - Long.BYTES];
        for (int i = 0; i < data.length; i++) {
            data[i] = received[i + Long.BYTES];
        }
        return data;
    }

    private long retrieveTimestamp(byte[] data) {
        byte[] stampBytes = new byte[Long.BYTES];
        for (int i = 0; i < Long.BYTES; i++) {
            stampBytes[i] = data[i];
        }
        return Helper.bytesToLong(stampBytes);
    }

    private boolean checkTimestamp(long timestamp) {
        long reference = timestampReference.toEpochMilli() + timestamp;
        long current = Instant.now().toEpochMilli();
        if(current - reference > TIMESTAMP_THRESHOLD || reference > current)
            return false;
        return true;
    }

    // This 16 byte key is printed on the smartHomeDevice
    String printedKey = "ABCDEFGHIJKLMNOP";

}
