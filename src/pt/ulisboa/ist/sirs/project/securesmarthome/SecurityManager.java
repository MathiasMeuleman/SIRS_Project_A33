package pt.ulisboa.ist.sirs.project.securesmarthome;


import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import javax.crypto.SecretKey;
import java.io.IOException;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.Arrays;

/**
 * Created by Mathias on 2016-11-21.
 */
public abstract class SecurityManager {

    private static final long TIMESTAMP_THRESHOLD = Long.MAX_VALUE;

    protected final String TIME_SERVER = "128.138.141.172";
    protected long timeRef;
    protected SocketChannel commChannel;
    protected byte[] publicSHDKey;
    protected byte[] publicGatewayKey;
    protected SecretKey sessionKey;
    protected SecretKey aprioriSharedKey;

    public SecurityManager() {

    }

    public void connectToDevice() {
        shareSessionKey();
        initTimestamp();
        System.out.println("TimeRef: " + timeRef);
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
            System.out.println("ToSend: " + Arrays.toString(encrypted));
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
        } else {
            System.out.println("Timestamp check failed");
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
            } else {
                System.out.println("Timestamp check failed");
                return null;
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

    public void initTimestamp() {
        try {
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress address = InetAddress.getByAddress(TIME_SERVER.getBytes());
            TimeInfo info = timeClient.getTime(address);
            long exactTime = info.getReturnTime();
            long currentTime = System.currentTimeMillis();
            timeRef = currentTime - exactTime;
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
        System.out.println("Timestamp check: ");
        System.out.println("Received: " + timestamp);
        System.out.println("Current: " + current);
        if(current - timestamp > TIMESTAMP_THRESHOLD || timestamp > current)
            return false;
        return true;
    }
}
