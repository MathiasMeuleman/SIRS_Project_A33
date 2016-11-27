package pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement;

import javax.crypto.SecretKey;
import java.io.*;
import java.security.*;
import java.security.cert.*;


/**
 * Created by Mathias on 2016-11-21.
 */
public class GenericKeyStore {

    private KeyStore store;
    private String password = "password";
    private String keystorePath = "doc/storage.txt";

    public GenericKeyStore() {
        try {
            store = KeyStore.getInstance("JCEKS");
            store.load(null, null);
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void storeKey(Key key, String alias) throws Exception {
        OutputStream stream = new FileOutputStream(keystorePath);
        store.setKeyEntry(alias, key, password.toCharArray(), null);
        store.store(stream, password.toCharArray());
        stream.close();
    }

    public SecretKey loadKey(String alias) throws Exception {
        InputStream stream = new FileInputStream(keystorePath);
        store.load(stream, password.toCharArray());
        stream.close();
        Key key = store.getKey(alias, password.toCharArray());
        if(key instanceof SecretKey) {
            return (SecretKey) key;
        } else {
            System.out.println("Key is not a SecretKey");
            throw new ClassCastException();
        }
    }
}
