package pt.ulisboa.ist.sirs.project.securesmarthome;

import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreement2;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESKeyGenerator;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.GenericKeyStore;

import javax.crypto.SecretKey;

/**
 * Created by Alex Anders on 18/11/2016.
 */
public class Main {
    public static void main(String argv[]) throws Exception {
        AESKeyGenerator gen = new AESKeyGenerator();
        SecretKey key = gen.generateKey();
        GenericKeyStore store = new GenericKeyStore();
        store.storeKey(key, "key1");
        SecretKey key2 = store.loadKey("key1");
        System.out.println("(key1 == key2) = " + key.equals(key2));

//        try {
//            String mode = "USE_SKIP_DH_PARAMS";
//
//            DHKeyAgreement2 keyAgree = new DHKeyAgreement2();
//
//            if (argv.length > 1) {
//                keyAgree.usage();
//                throw new Exception("Wrong number of command options");
//            } else if (argv.length == 1) {
//                if (!(argv[0].equals("-gen"))) {
//                    keyAgree.usage();
//                    throw new Exception("Unrecognized flag: " + argv[0]);
//                }
//                mode = "GENERATE_DH_PARAMS";
//            }
//
//            keyAgree.run(mode);
//        } catch (Exception e) {
//            System.err.println("Error: " + e);
//            System.exit(1);
//        }
    }
}
