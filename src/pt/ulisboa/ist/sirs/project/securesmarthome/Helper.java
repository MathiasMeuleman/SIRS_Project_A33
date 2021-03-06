package pt.ulisboa.ist.sirs.project.securesmarthome;

import org.apache.commons.net.ntp.NTPUDPClient;
import org.apache.commons.net.ntp.TimeInfo;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Helper {
    /*
     * Converts a byte to hex digit and writes to the supplied buffer
     */
    public static void byte2hex(byte b, StringBuffer buf) {
        char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
                '9', 'A', 'B', 'C', 'D', 'E', 'F' };
        int high = ((b & 0xf0) >> 4);
        int low = (b & 0x0f);
        buf.append(hexChars[high]);
        buf.append(hexChars[low]);
    }

    public static byte[] getConcatPubKeys(byte[] pubKeyEncA, byte[] pubKeyEncB) {
        // concatenating the public values
        byte[] concatPubKeyAB = new byte[pubKeyEncA.length + pubKeyEncB.length];
        System.arraycopy(pubKeyEncA, 0, concatPubKeyAB, 0, pubKeyEncA.length);
        System.arraycopy(pubKeyEncB, 0, concatPubKeyAB, pubKeyEncA.length, pubKeyEncB.length);

        return concatPubKeyAB;
    }

    private static ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);

    /**
     * Convert long to bytes
     * @param x
     * @return
     */
    public static byte[] longToBytes(long x) {
        buffer.putLong(0, x);
        return buffer.array();
    }

    /**
     * Convert bytes to long
     * @param bytes
     * @return
     */
    public static long bytesToLong(byte[] bytes) {
        buffer.put(bytes, 0, Long.BYTES);
        buffer.flip();//need flip
        long res = buffer.getLong();
        buffer.clear();
        return res;
    }

    public static final String TIME_SERVER = "98.175.203.200";

    public static long initTimestamp() {
        try {
            NTPUDPClient timeClient = new NTPUDPClient();
            InetAddress address = InetAddress.getByName(TIME_SERVER);
            TimeInfo info = timeClient.getTime(address);
            info.computeDetails();
            return info.getOffset();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
