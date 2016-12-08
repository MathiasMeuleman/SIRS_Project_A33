package pt.ulisboa.ist.sirs.project.securesmarthome;

import javax.crypto.SecretKey;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * Created by Mathias on 2016-11-21.
 */
public abstract class SecurityManager {

    private static final long TIMESTAMP_THRESHOLD = 1000;

    protected final int KEY_THRESHOLD = 25;

    protected long timeRef;
    protected SocketChannel commChannel;
    protected byte[] publicSHDKey;
    protected byte[] publicGatewayKey;
    protected SecretKey sessionKey;
    protected SecretKey aprioriSharedKey;
    protected int keyUsageCounter = 0;

    public void connectToDevice() {
        shareSessionKey();
        System.out.println("[INFO] Shared session key");
        shareIV();
        System.out.println("[INFO] Shared IV");
        authenticate();
    }

    public abstract void shareUUID();
    public abstract void shareSessionKey();
    public abstract void shareIV();
    public abstract void authenticate();
    public abstract void checkKeyExpired() throws SocketException;

    public void send(byte[] data) throws SocketException {
        byte[] toSend = addTimestamp(data);
        byte[] encrypted = Cryptography.encrypt(toSend, sessionKey, "CBC");
        System.out.println("Sending encrypted: " + Arrays.toString(encrypted));
        commChannel.sendMessage(encrypted);
        checkKeyExpired();
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
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
        checkKeyExpired();
        if(checkTimestamp(timestamp)) {
            return retrieveData(received);
        } else {
            System.out.println("Timestamp check failed");
        }
        return null;
    }

    public byte[] receiveEncrypted(String mode) throws TimeoutException {
        return receiveEncrypted(sessionKey, mode);
    }

    public byte[] receiveEncrypted(SecretKey key, String mode) throws TimeoutException{
        try {
            long timeoutMilliseconds = 10000;
            byte[] encrypted = Executor.exeSocketChannelReceive(commChannel,timeoutMilliseconds);
            /* code without timeout
            byte[] encrypted = commChannel.receiveByteArray();
            **/
            byte[] received = Cryptography.decrypt(encrypted, key, mode);
            long timestamp = retrieveTimestamp(received);
            if(checkTimestamp(timestamp)) {
                return retrieveData(received);
            } else {
                System.out.println("Timestamp check failed");
                return null;
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null;
    }

        public byte[] receiveUnsecured() throws TimeoutException{
        try {
            long timeoutMilliseconds = 10000;
            return Executor.exeSocketChannelReceive(commChannel,timeoutMilliseconds);
            /* code without timeout
            return commChannel.receiveByteArray();
            */
        } catch (SocketException e) {
            e.printStackTrace();
        }
            return null;
    }

    public byte[] addTimestamp(byte[] data) {
        long timestamp = System.currentTimeMillis() + timeRef;
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
        long current = System.currentTimeMillis() + timeRef;
        System.out.println("Timestamp check");
        System.out.println("Current: "+ current);
        System.out.println("Received: " + timestamp);
        if(current - timestamp > TIMESTAMP_THRESHOLD || timestamp - 10 > current)
            return false;
        return true;
    }

    protected boolean clientOnSameIP() {
        InetSocketAddress local = (InetSocketAddress) this.commChannel.getLocalAddress();
        InetSocketAddress remote = (InetSocketAddress) this.commChannel.getRemoteAddress();
        return local.getAddress().equals(remote.getAddress());
    }
}
