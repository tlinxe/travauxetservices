package fr.travauxetservices.tools;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.crypto.*;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class Encryptor {


    // the key
    private transient SecretKey key;

    /**
     * Constructor creates secret key. In production we may want
     * to avoid keeping the secret key hanging around in memory for
     * very long.
     */
    private void generateKey() throws NoSuchAlgorithmException {
        KeyGenerator generator;
        generator = KeyGenerator.getInstance("DES");
        generator.init(new SecureRandom());
        key = generator.generateKey();
    }

    /**
     * Decrypt String
     */
    public String decrypt(String encrypted) throws InvalidKeyException,
            NoSuchAlgorithmException, NoSuchPaddingException,
            IllegalBlockSizeException, BadPaddingException, IOException {

        // Get a cipher object.
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);

        //decode the BASE64 coded message
        BASE64Decoder decoder = new BASE64Decoder();
        byte[] raw = decoder.decodeBuffer(encrypted);

        //decode the message
        byte[] stringBytes = cipher.doFinal(raw);

        //converts the decoded message to a String
        String clear = new String(stringBytes, "UTF8");
        return clear;
    }

    /**
     * Encrypt String
     */
    public String encrypt(String message) throws IllegalBlockSizeException,
            BadPaddingException, NoSuchAlgorithmException,
            NoSuchPaddingException, InvalidKeyException,
            UnsupportedEncodingException {
        // Get a cipher object.
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);

        // Gets the raw bytes to encrypt, UTF8 is needed for
        // having a standard character set
        byte[] stringBytes = message.getBytes("UTF8");

        // encrypt using the cypher
        byte[] raw = cipher.doFinal(stringBytes);

        // converts to base64 for easier display.
        BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encode(raw);

        return base64;
    }
}
