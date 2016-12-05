package pt.ulisboa.ist.sirs.project.securesmarthome;

import javax.crypto.SecretKey;
import java.net.SocketException;
import java.time.Instant;


/**
 * Created by Mathias on 2016-11-21.
 */
public abstract class SecurityManager {

    private static final long TIMESTAMP_THRESHOLD = Long.MAX_VALUE;

    protected SocketChannel commChannel;
    protected byte[] publicSHDKey;
    protected byte[] publicGatewayKey;
    protected SecretKey sessionKey;
    protected SecretKey aprioriSharedKey;

    public SecurityManager() {

    }

    public void connectToDevice() {
        shareSessionKey();
        shareIV();
        authenticate();
    }

    public abstract void shareSessionKey();
    public abstract void shareIV();
    public abstract void authenticate();

    public void send(byte[] data) throws SocketException {
        byte[] toSend = addTimestamp(data);
        byte[] encrypted = Cryptography.encrypt(toSend, sessionKey, "CBC");
        commChannel.sendMessage(encrypted);
    }

    public void sendEncrypted(byte[] data, String mode) {
        sendEncrypted(data, sessionKey, mode);
    }

    public void sendEncrypted(byte[] data, SecretKey key, String mode) {
        byte[] toSend = addTimestamp(data);
        byte[] encrypted = Cryptography.encrypt(toSend, key, mode);
        try {
            commChannel.sendMessage(encrypted);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public void sendUnsecured(byte[] data) {
        try {
            commChannel.sendMessage(data);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public byte[] receive() throws SocketException {
        byte[] encrypted = commChannel.receiveByteArray();
        byte[] received = Cryptography.decrypt(encrypted, sessionKey, "CBC");
        long timestamp = retrieveTimestamp(received);
        if(checkTimestamp(timestamp)) {
            return retrieveData(received);
        }
        return null;
    }

    public byte[] receiveEncrypted(String mode) {
        return receiveEncrypted(sessionKey, mode);
    }

    public byte[] receiveEncrypted(SecretKey key, String mode) {
        try {
            byte[] encrypted = commChannel.receiveByteArray();
            byte[] received = Cryptography.decrypt(encrypted, key, mode);
            long timestamp = retrieveTimestamp(received);
            if(checkTimestamp(timestamp)) {
                return retrieveData(received);
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    public byte[] receiveUnsecured() {
        try {
            return commChannel.receiveByteArray();
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Prepend current timestamp to the data
     * @param data
     * @return
     */
    public byte[] addTimestamp(byte[] data) {
        Instant inst = Instant.now();
        long timestamp = inst.toEpochMilli();
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
        long current = Instant.now().toEpochMilli();
        if(current - timestamp > TIMESTAMP_THRESHOLD || timestamp > current)
            return false;
        return true;
    }
}
