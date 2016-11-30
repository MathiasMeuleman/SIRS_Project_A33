package pt.ulisboa.ist.sirs.project.securesmarthome.smarthomedevice;

import pt.ulisboa.ist.sirs.project.securesmarthome.communication.CommunicationMode;
import pt.ulisboa.ist.sirs.project.securesmarthome.communication.SocketChannel;
import pt.ulisboa.ist.sirs.project.securesmarthome.Device;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.Authentication;
import pt.ulisboa.ist.sirs.project.securesmarthome.diffiehellman.DHKeyAgreementSHD;
import pt.ulisboa.ist.sirs.project.securesmarthome.keymanagement.AESSecretKeyFactory;


/**
 * Created by Mathias on 2016-11-21.
 */
public class SmartHomeDevice extends Device{

    public SmartHomeDevice(CommunicationMode commMode) {
        super(commMode);

        aprioriSharedKey = AESSecretKeyFactory.createSecretKey(printedKey);

        dh = new DHKeyAgreementSHD();
        dhKeyAgreement();
        // authentication part
        authenticationMessageEncrypted = Authentication.getConcatPubKeyABEncrypted(
                aprioriSharedKey, pubKeyEncA, pubKeyEncB);

        //
    }

    // This 16 byte key is printed on the smartHomeDevice
    byte[] printedKey =
            {(byte)0xF4, (byte)0x88, (byte)0xFD, (byte)0x58,
            (byte)0x4E, (byte)0x49, (byte)0xDB, (byte)0xCD,
            (byte)0x20, (byte)0xB4, (byte)0x9D, (byte)0xE4,
            (byte)0x91, (byte)0x07, (byte)0x36, (byte)0x6B};
}
