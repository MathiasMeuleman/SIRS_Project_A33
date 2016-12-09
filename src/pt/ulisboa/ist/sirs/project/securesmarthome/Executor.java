package pt.ulisboa.ist.sirs.project.securesmarthome;

import java.net.SocketException;
import java.sql.Time;
import java.util.Arrays;
import java.util.concurrent.*;

public class Executor {
    public static byte[] exeSocketChannelReceive
            (SocketChannel channel, long timeoutMilliseconds) throws SocketException,TimeoutException {
        ExecutorService executor = Executors.newCachedThreadPool();
        Callable<Object> task = () -> channel.receiveByteArray();
        Future<Object> future = executor.submit(task);
        try {
            Object result = future.get(timeoutMilliseconds, TimeUnit.MILLISECONDS);
            return (byte[])result;
        } catch (TimeoutException e){
            throw new TimeoutException();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } finally {
            future.cancel(true); // may or may not desire this
        }
        return null;
    }
}
