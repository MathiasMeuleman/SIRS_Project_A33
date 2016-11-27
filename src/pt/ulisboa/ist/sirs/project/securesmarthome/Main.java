package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SimpleChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESKeyGenerator;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.GenericKeyStore;

import javax.crypto.SecretKey;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.Device;

/**
 * Created by Alex Anders on 18/11/2016.
 */
public class Main {
    public static void main(String[] args) throws Exception {
//        AESKeyGenerator gen = new AESKeyGenerator();
//        SecretKey key = gen.generateKey();
//        System.out.println(key.getEncoded());
//        GenericKeyStore store = new GenericKeyStore();
//        store.storeKey(key, "key1");
//        SecretKey key2 = store.loadKey("key1");
//        System.out.println(key2.getEncoded());


        // channel to be used for communication between smart home parties
        SimpleChannel simpleChannel = new SimpleChannel();

        if ((args.length > 2) || (args.length < 1)) {
            throw new Exception("Wrong number of command options");
        }

        if (args[0].equals("gateway")) {
            Device gateway = new Device();
            gateway.dhKeyAgreement(args[1], simpleChannel);
        }
        if (args[0].equals("smartHomeDevice")) {
            Device smartHomeDevice = new Device();
            smartHomeDevice.dhKeyAgreement(args[1], simpleChannel);
        }
    }
}