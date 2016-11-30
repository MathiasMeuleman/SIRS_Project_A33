package pt.ulisboa.ist.sirs.project.securesmarthome.gateway;

import pt.ulisboa.ist.sirs.project.securesmarthome.Helper;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreementGateway;
import pt.ulisboa.ist.sirs.project.securesmarthome.encryption.Cryptography;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Mathias on 2016-11-21.
 */
public class Gateway extends Device{

    public Gateway(CommunicationMode commMode, String key) {
        super(commMode);
        smartHomeDevices = new ArrayList<>();
        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(key);

        dh = new DHKeyAgreementGateway();
        dhKeyAgreement();
        System.out.println("Finished DH");
        // authentication part
        // receive authentication message from SHD
        authenticationMessageEncrypted = commChannel.receiveByteArray();
        // decrypt it
        authenticationMessage = Cryptography.decrypt(authenticationMessageEncrypted,aprioriSharedKey);
        // compare with concatenated keys
        byte[] concatKeys = Helper.getConcatPubKeys(pubKeyEncA, pubKeyEncB);
        if (Arrays.equals(authenticationMessage, concatKeys)) {
            smartHomeDevices.add(new AuthenticatedSHD(true));
            // authenticate gateway
            // generate authentication message
            authenticationMessage = Helper.getConcatPubKeys(pubKeyEncB, pubKeyEncA);
            // encrypt authentication message
            authenticationMessageEncrypted = Cryptography.encrypt(authenticationMessage, aprioriSharedKey);
            // authenticate by sending it to the other party
            commChannel.sendMessage(authenticationMessageEncrypted);
        }
        else
            smartHomeDevices.add(new AuthenticatedSHD(false));
    }
    private List<AuthenticatedSHD> smartHomeDevices;
    public void setCommunicationChannel() {
        commChannel = new SocketChannel(CommunicationMode.GATEWAY);
    }

}
