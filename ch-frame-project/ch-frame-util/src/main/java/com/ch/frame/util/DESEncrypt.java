package com.ch.frame.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import java.io.IOException;
import java.util.UUID;

public class DESEncrypt {
	
    public static void main(String[] args) {
    	
    	String key = RsaDesUtils.genRandomKey(8);
        String text = "测试asdY^&*NN!__s some plaintext!asfdsaljdvnomvowmoimowiemvoimsOfim[onfoi2ngoin240j0t294jg00d9g0jg0j0mv0mg09j测试asdY^&*NN!__s some plaintext!asfdsaljdvnomvowmoimowiemvoimsOfim[onfoi2ngoin240j0t294jg00d9g0jg0j0mv0mg09j测试asdY^&*NN!__s some plaintext!asfdsaljdvnomvowmoimowiemvoimsOfim[onfoi2ngoin240j0t294jg00d9g0jg0j0mv0mg09j";
        System.out.println("加密前的明文:" + text);

        String cryperText = "";
        try {
            cryperText = toHexString(encrypt(key,text));
            System.out.println("加密后的明文:" + cryperText);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            System.out.println("解密后的明文:" + decrypt(key,cryperText));
        } catch (Exception e) {
            e.printStackTrace();
        }
        /*try {
            System.in.read();
        } catch (IOException e) {
            e.printStackTrace();
        }*/

        System.out.println(UUID.randomUUID());
    }

    public static String decrypt(String key,String message) throws Exception {

        byte[] bytesrc = convertHexString(message);
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");
        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));

        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);

        byte[] retByte = cipher.doFinal(bytesrc);
        return new String(retByte);
    }

    public static byte[] encrypt(String key,String message) throws Exception {
        Cipher cipher = Cipher.getInstance("DES/CBC/PKCS5Padding");

        DESKeySpec desKeySpec = new DESKeySpec(key.getBytes("UTF-8"));

        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey secretKey = keyFactory.generateSecret(desKeySpec);
        IvParameterSpec iv = new IvParameterSpec(key.getBytes("UTF-8"));
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);

        return cipher.doFinal(message.getBytes("UTF-8"));
    }

    public static byte[] convertHexString(String ss) {
        byte digest[] = new byte[ss.length() / 2];
        for (int i = 0; i < digest.length; i++) {
            String byteString = ss.substring(2 * i, 2 * i + 2);
            int byteValue = Integer.parseInt(byteString, 16);
            digest[i] = (byte) byteValue;
        }

        return digest;
    }

    public static String toHexString(byte b[]) {
        StringBuffer hexString = new StringBuffer();
        for (int i = 0; i < b.length; i++) {
            String plainText = Integer.toHexString(0xff & b[i]);
            if (plainText.length() < 2)
                plainText = "0" + plainText;
            hexString.append(plainText);
        }
        return hexString.toString();
    }

}