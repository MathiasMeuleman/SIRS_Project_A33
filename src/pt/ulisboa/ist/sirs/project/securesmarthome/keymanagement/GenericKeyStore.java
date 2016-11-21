package pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement;

import javax.crypto.SecretKey;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.KeyStore;
import java.security.KeyStore.*;
import java.security.KeyStoreException;


/**
 * Created by Mathias on 2016-11-21.
 */
public class GenericKeyStore {

    private KeyStore store;
    private String password = "password";
    private String keystorePath = "doc/storage.txt";

    public GenericKeyStore() {
        try {
            store = KeyStore.getInstance(KeyStore.getDefaultType());
        } catch (KeyStoreException e) {
            e.printStackTrace();
        }
    }

    public void storeKey(SecretKey key, String alias) throws Exception {
        OutputStream stream = new FileOutputStream(keystorePath);
        System.out.println(store);
        store.setEntry(alias, new SecretKeyEntry(key), null);
        store.store(stream, password.toCharArray());
    }

    public SecretKey loadKey(String alias) throws Exception {
        InputStream stream = new FileInputStream(keystorePath);
        store.load(stream, password.toCharArray());
        return ((SecretKeyEntry)store.getEntry(alias, null)).getSecretKey();
    }
}
