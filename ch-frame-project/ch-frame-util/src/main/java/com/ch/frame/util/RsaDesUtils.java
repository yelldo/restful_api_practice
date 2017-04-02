package com.ch.frame.util;

import com.alibaba.fastjson.JSONObject;
import com.hx.frame.exception.GeneralException;

import java.util.Map;
import java.util.Random;

/**
 * RSA结合DES加密
 *
 * @author linsq
 */
public class RsaDesUtils {
    public static void main(String[] args) throws Exception {
        String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCaVBkhtgr1eRntFdsFtRLZ wIVMol48OhUe+89RQNn7E89GBzuusvUNb6b0sc2BvSr+Jf3NYefUM5Mu3aRD RJNqXofwj2xP2j2NarQYJbSs6wDuvTgB1XIjlekJCjqIGIxh0lHeL38ijLyT b2I/vvs2ONSOPDITOrBrIWaTtgOEgwIDAQAB";
        String privateKey = "MIICeQIBADANBgkqhkiG9w0BAQEFAASCAmMwggJfAgEAAoGBAJpUGSG2CvV5 Ge0V2wW1EtnAhUyiXjw6FR77z1FA2fsTz0YHO66y9Q1vpvSxzYG9Kv4l/c1h 59Qzky7dpENEk2peh/CPbE/aPY1qtBgltKzrAO69OAHVciOV6QkKOogYjGHS Ud4vfyKMvJNvYj+++zY41I48MhM6sGshZpO2A4SDAgMBAAECgYEAkQUV2KfI BIOX6UHwXpyY6iYPHMQ26UFrN4JIfoICU/15eMSzQKbJv6Z7rNSn2JJ6I/hv Vt/5iV4toIyGHvAB1SEOebp70JepwpV91oJRnWbKZB+PB1N+VUgNEXlmnD6f pNfWa65BKDYHqcTq9ppZtpTCwU+jhHp7vPYOtbB+VYECQQD/85DBY1IBKFJt 3gO+3gwjac6zlTm++a7vpcqCi6jo4dbROfe9j7tXvU5tLeRhKzDRSWgdhTqA lnAY2LHkM2dnAkEAmluYgFPWMW7HIQQwM9t8B9+1pUIL2g4ijHJcXdBvsT8M EhLDo/bkWbYs0BorlvGr9CzPtl2/hxY5nNMjM/dUhQJBAM8wH/6b4pbzR/6N 2KRZ5KOKE/vFEVCpl1WA9HRGj32syxLlBMlbH0EACgtROez6ZHMWJAS5g0jx /l3uk9nxToMCQQCJrhmgnSb4gK7tLCcymhtPiqMi/H5FyUXXsEnjBAAcQPgr RsFqhtW3j175X95xjK6r5SyApa4oeBJ7Z3iG6Z1hAkEA00Ag6rlLj//xqmzg rjTiHzTuL8eOZ/3d3RWrtJabG7A4H8ewtX2ELxdApuZwCGeCrQ3dcTmsBJPH 8XvNSajH5g==";
        String content = "hello world!";

        JSONObject aData = new JSONObject();
        aData.put("content", content);
        //加密
        String miwen = RsaDesUtils.encrypt(publicKey, aData);
        System.out.println("发送方加密后的密文数据:" + miwen);

        //解密
        JSONObject bData = RsaDesUtils.decrypt(privateKey, miwen);
        String rContent = bData.getString("content");
        System.out.println("接收方解密后明文数据:" + rContent);
    }

    /**
     * 生成一对新的公钥私钥,控制台打印出来
     *
     * @throws Exception
     */
    public static void printKeyPair() throws Exception {
        Map<String, Object> keyMap = RSAUtils.genKeyPair();
        System.err.println("公钥: \n\r" + RSAUtils.getPublicKey(keyMap));
        System.err.println("私钥： \n\r" + RSAUtils.getPrivateKey(keyMap));
    }

    /**
     * 生成随机字符
     *
     * @param length 字符长度
     * @return
     */
    public static String genRandomKey(int length) {
        String base = "1234567890abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < length; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    /**
     * 加密
     *
     * @param publicKey
     * @param data
     * @return
     */
    public static String encrypt(String publicKey, JSONObject data) {
        try {
            String desKey = genRandomKey(8);//随机生成8位DES密钥
            //使用RSA公钥对KEY进行加密
            byte[] encodedData = RSAUtils.encryptByPublicKey(desKey.getBytes(), publicKey);
            //使用DES密钥对内容进行加密
            String encrDesData = DESEncrypt.toHexString(DESEncrypt.encrypt(desKey, data.toJSONString()));

            JSONObject obj = new JSONObject();
            obj.put("key", Base64.encode(encodedData));
            obj.put("data", encrDesData);
            return obj.toJSONString();
        } catch (Exception e) {
            throw new GeneralException(e);
        }
    }

    /**
     * 解密
     *
     * @param privateKey
     * @param str
     * @return
     */
    public static JSONObject decrypt(String privateKey, String str) {
        try {
            JSONObject dataObj = JSONObject.parseObject(str);
            String keyStr = dataObj.getString("key");
            String dataStr = dataObj.getString("data");
            //使用RSA私钥进行KEY解密，获得DES密钥
            String desKey = new String(RSAUtils.decryptByPrivateKey(Base64.decode(keyStr), privateKey));
            //使用DES密钥对内容进行解密
            String data = DESEncrypt.decrypt(desKey, dataStr);

            return JSONObject.parseObject(data);
        } catch (Exception e) {
            throw new GeneralException(e);
        }
    }
}