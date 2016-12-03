package pt.ulisboa.ist.sirs.project.securesmarthome;

/*
 * Based on Oracle software:
 *
 * Copyright (c) 1997, 2001, Oracle and/or its affiliates. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 *   - Redistributions of source code must retain the above copyright
 *     notice, this list of conditions and the following disclaimer.
 *
 *   - Redistributions in binary form must reproduce the above copyright
 *     notice, this list of conditions and the following disclaimer in the
 *     documentation and/or other materials provided with the distribution.
 *
 *   - Neither the name of Oracle nor the names of its
 *     contributors may be used to endorse or promote products derived
 *     from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS
 * IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

import javax.crypto.KeyAgreement;
import javax.crypto.SecretKey;
import javax.crypto.interfaces.DHPublicKey;
import javax.crypto.spec.DHParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.X509EncodedKeySpec;

/**
 * This program executes the Diffie-Hellman key agreement protocol
 * between 2 parties: Alice and Bob.
 * <p>
 * By default, preconfigured parameters (1024-bit prime modulus and base
 * generator used by SKIP) are used.
 * If this program is called with the "-gen" option, a new set of
 * parameters is created.
 */

public class DHKeyAgreement {

    public static byte[] getPublicSHDKey(String argv) {
        try {
            String paramsMode = "USE_SKIP_DH_PARAMS";

            if (argv != null) {
                if (!(argv.equals("-gen"))) {
                    usage();
                    throw new Exception("Unrecognized flag: " + argv);
                }
                paramsMode = "GENERATE_DH_PARAMS";
            }

            // generating pubKeyEncAlice
            byte[] pubKeyEncA = getPubKeyEncAlice(paramsMode);
            return pubKeyEncA;
        } catch (Exception e) {
            System.err.println("Error: " + e);
            System.exit(1);
            return null;
        }
    }

    public static byte[] getPublicGatewayKey(byte[] pubKeyEncAlice) {
        try {
            // generating pubKeyEncBob
            byte[] pubKeyEncB = getPubKeyEncBob(pubKeyEncAlice);
            return pubKeyEncB;
        } catch (Exception e) {
            System.err.println("Error: " + e);
            System.exit(1);
            return null;
        }
    }

    public static byte[] getPubKeyEncAlice(String mode) throws Exception {

        DHParameterSpec dhSkipParamSpec;

        if (mode.equals("GENERATE_DH_PARAMS")) {
            // Some central authority creates new DH parameters
            System.out.println
                    ("Creating Diffie-Hellman parameters (takes VERY long) ...");
            AlgorithmParameterGenerator paramGen
                    = AlgorithmParameterGenerator.getInstance("DH");
            paramGen.init(512);
            AlgorithmParameters params = paramGen.generateParameters();
            dhSkipParamSpec = (DHParameterSpec) params.getParameterSpec
                    (DHParameterSpec.class);
        } else {
            // use some pre-generated, default DH parameters
            System.out.println("Using SKIP Diffie-Hellman parameters");
            dhSkipParamSpec = new DHParameterSpec(skip1024Modulus,
                    skip1024Base);
        }

        /*
         * Alice creates her own DH key pair, using the DH parameters from
         * above
         */
        System.out.println("ALICE: Generate DH keypair ...");
        KeyPairGenerator aliceKpairGen = KeyPairGenerator.getInstance("DH");
        aliceKpairGen.initialize(dhSkipParamSpec);
        KeyPair aliceKpair = aliceKpairGen.generateKeyPair();

        // Alice creates and initializes her DH KeyAgreement object
        System.out.println("ALICE: Initialization ...");
        aliceKeyAgree = KeyAgreement.getInstance("DH");
        aliceKeyAgree.init(aliceKpair.getPrivate(), dhSkipParamSpec);

        // Alice encodes her public key, and sends it over to Bob.
        byte[] alicePubKeyEnc = aliceKpair.getPublic().getEncoded();
        return alicePubKeyEnc;
    }


    public static byte[] getPubKeyEncBob(byte[] pubKeyEncAlice) throws Exception {
        /*
         * Let's turn over to Bob. Bob has received Alice's public key
         * in encoded format.
         * He instantiates a DH public key from the encoded key material.
         */
        KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
        x509KeySpec = new X509EncodedKeySpec(pubKeyEncAlice);
        PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);

        /*
         * Bob gets the DH parameters associated with Alice's public key.
         * He must use the same parameters when he generates his own key
         * pair.
         */
        DHParameterSpec dhParamSpec = ((DHPublicKey) alicePubKey).getParams();

        // Bob creates his own DH key pair
        System.out.println("BOB: Generate DH keypair ...");
        KeyPairGenerator bobKpairGen = KeyPairGenerator.getInstance("DH");
        bobKpairGen.initialize(dhParamSpec);
        KeyPair bobKpair = bobKpairGen.generateKeyPair();

        // Bob creates and initializes his DH KeyAgreement object
        System.out.println("BOB: Initialization ...");
        bobKeyAgree = KeyAgreement.getInstance("DH");
        bobKeyAgree.init(bobKpair.getPrivate());

        // Bob encodes his public key, and sends it over to Alice.
        byte[] bobPubKeyEnc = bobKpair.getPublic().getEncoded();
        return bobPubKeyEnc;
    }

    public static void createSharedSecretA(byte[] pubKeyEncBob) {
        /*
         * Alice uses Bob's public key for the first (and only) phase
         * of her version of the DH
         * protocol.
         * Before she can do so, she has to instantiate a DH public key
         * from Bob's encoded key material.
         */
        try {
            KeyFactory aliceKeyFac = KeyFactory.getInstance("DH");
            x509KeySpec = new X509EncodedKeySpec(pubKeyEncBob);
            PublicKey bobPubKey = aliceKeyFac.generatePublic(x509KeySpec);
            System.out.println("ALICE: Execute PHASE1 ...");
            aliceKeyAgree.doPhase(bobPubKey, true);
            sharedSecretKey = aliceKeyAgree.generateSecret("AES");
            resizeKey();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static void createSharedSecretB(byte[] pubKeyEncAlice) {
        /*
         * Bob uses Alice's public key for the first (and only) phase
         * of his version of the DH
         * protocol.
         * Before he can do so, he has to instantiate a DH public key
         * from Alice's encoded key material.
         * Actually he has done this already before, but to keep the code clearer
         * it is solved this way.
         */
        try {
            KeyFactory bobKeyFac = KeyFactory.getInstance("DH");
            x509KeySpec = new X509EncodedKeySpec(pubKeyEncAlice);
            PublicKey alicePubKey = bobKeyFac.generatePublic(x509KeySpec);
            System.out.println("BOB: Execute PHASE1 ...");
            bobKeyAgree.doPhase(alicePubKey, true);
            sharedSecretKey = bobKeyAgree.generateSecret("AES");
            resizeKey();
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Assuming a key > 128 bits (16 bytes), needs to be resized to 128 bits (16 bytes)
     */
    private static void resizeKey() {
        byte[] key = sharedSecretKey.getEncoded();
        byte[] resizedKeyBytes = new byte[16];
        for(int i = 0; i< resizedKeyBytes.length; i++) {
            resizedKeyBytes[i] = key[i];
        }
        sharedSecretKey = new SecretKeySpec(resizedKeyBytes, "AES");
    }

    public static SecretKey getSharedSecretKey() {
        return sharedSecretKey;
    }


    /*
     * Prints the usage of this test.
     */
    public static void usage() {
        System.err.print("DHKeyAgreement usage: ");
        System.err.println("[-gen]");
    }

    // The 1024 bit Diffie-Hellman modulus values used by SKIP
    private static final byte skip1024ModulusBytes[] = {
            (byte) 0xF4, (byte) 0x88, (byte) 0xFD, (byte) 0x58,
            (byte) 0x4E, (byte) 0x49, (byte) 0xDB, (byte) 0xCD,
            (byte) 0x20, (byte) 0xB4, (byte) 0x9D, (byte) 0xE4,
            (byte) 0x91, (byte) 0x07, (byte) 0x36, (byte) 0x6B,
            (byte) 0x33, (byte) 0x6C, (byte) 0x38, (byte) 0x0D,
            (byte) 0x45, (byte) 0x1D, (byte) 0x0F, (byte) 0x7C,
            (byte) 0x88, (byte) 0xB3, (byte) 0x1C, (byte) 0x7C,
            (byte) 0x5B, (byte) 0x2D, (byte) 0x8E, (byte) 0xF6,
            (byte) 0xF3, (byte) 0xC9, (byte) 0x23, (byte) 0xC0,
            (byte) 0x43, (byte) 0xF0, (byte) 0xA5, (byte) 0x5B,
            (byte) 0x18, (byte) 0x8D, (byte) 0x8E, (byte) 0xBB,
            (byte) 0x55, (byte) 0x8C, (byte) 0xB8, (byte) 0x5D,
            (byte) 0x38, (byte) 0xD3, (byte) 0x34, (byte) 0xFD,
            (byte) 0x7C, (byte) 0x17, (byte) 0x57, (byte) 0x43,
            (byte) 0xA3, (byte) 0x1D, (byte) 0x18, (byte) 0x6C,
            (byte) 0xDE, (byte) 0x33, (byte) 0x21, (byte) 0x2C,
            (byte) 0xB5, (byte) 0x2A, (byte) 0xFF, (byte) 0x3C,
            (byte) 0xE1, (byte) 0xB1, (byte) 0x29, (byte) 0x40,
            (byte) 0x18, (byte) 0x11, (byte) 0x8D, (byte) 0x7C,
            (byte) 0x84, (byte) 0xA7, (byte) 0x0A, (byte) 0x72,
            (byte) 0xD6, (byte) 0x86, (byte) 0xC4, (byte) 0x03,
            (byte) 0x19, (byte) 0xC8, (byte) 0x07, (byte) 0x29,
            (byte) 0x7A, (byte) 0xCA, (byte) 0x95, (byte) 0x0C,
            (byte) 0xD9, (byte) 0x96, (byte) 0x9F, (byte) 0xAB,
            (byte) 0xD0, (byte) 0x0A, (byte) 0x50, (byte) 0x9B,
            (byte) 0x02, (byte) 0x46, (byte) 0xD3, (byte) 0x08,
            (byte) 0x3D, (byte) 0x66, (byte) 0xA4, (byte) 0x5D,
            (byte) 0x41, (byte) 0x9F, (byte) 0x9C, (byte) 0x7C,
            (byte) 0xBD, (byte) 0x89, (byte) 0x4B, (byte) 0x22,
            (byte) 0x19, (byte) 0x26, (byte) 0xBA, (byte) 0xAB,
            (byte) 0xA2, (byte) 0x5E, (byte) 0xC3, (byte) 0x55,
            (byte) 0xE9, (byte) 0x2F, (byte) 0x78, (byte) 0xC7
    };

    // The SKIP 1024 bit modulus
    private static final BigInteger skip1024Modulus
            = new BigInteger(1, skip1024ModulusBytes);

    // The base used with the SKIP 1024 bit modulus
    private static final BigInteger skip1024Base = BigInteger.valueOf(2);

    private static KeyAgreement aliceKeyAgree;
    private static KeyAgreement bobKeyAgree;
    private static X509EncodedKeySpec x509KeySpec;
    private static SecretKey sharedSecretKey;
}