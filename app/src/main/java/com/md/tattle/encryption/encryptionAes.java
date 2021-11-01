package com.md.tattle.encryption;

import android.util.Log;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class encryptionAes {

    private static byte keys [] = {9,115,51,86,105,4,-31,-23,68,88,17,20,3,-105,115,-53};
    private static Cipher cipher , decipher;
    private static SecretKeySpec secretKeySpec ;

    public static void initEncyrption (){

        try {
            cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            decipher = Cipher.getInstance("AES/ECB/PKCS5Padding" );
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        }

        secretKeySpec = new SecretKeySpec(keys , "AES");

    }

    public static String encrypt (String messagePlainText) {

        byte [] stringBytes = messagePlainText.getBytes();
        byte [] encryptedBytes = new byte[messagePlainText.length()];

        try {
            cipher.init(Cipher.ENCRYPT_MODE , secretKeySpec);
            encryptedBytes = cipher.doFinal(stringBytes);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        String cipherText = null;
        try {
            cipherText = new String(encryptedBytes  , "ISO-8859-1" );
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return cipherText;
    }

    public static String  decrypt (String cipherText) throws UnsupportedEncodingException {

        byte [] enceyptedByte = cipherText.getBytes("ISO-8859-1" );
        String decryptedString = cipherText;

        byte [] decryption;

        try {
            decipher.init(Cipher.DECRYPT_MODE , secretKeySpec);
            decryption = decipher.doFinal(enceyptedByte);
            decryptedString = new String(decryption);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }

        return decryptedString;
    }

}
