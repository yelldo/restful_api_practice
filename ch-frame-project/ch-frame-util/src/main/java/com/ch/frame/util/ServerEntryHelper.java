package com.ch.frame.util;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.CRC32;

/**
 * 服务器端加解密支持
 *
 * @author Administrator
 */
public class ServerEntryHelper {
    private static ServerEntryHelper inst;

    public synchronized static ServerEntryHelper get() {
        if (inst == null) {
            inst = new ServerEntryHelper();
        }
        return inst;
    }

    /**
     * 生成随机字符
     *
     * @return
     *//*
    private String genRandomKey(int length) {
  	  String base = "1234567890abcdefghijklmnopqrstuvwxyzQWERTYUIOPASDFGHJKLZXCVBNM~!@#$%^&*()";     
  	    Random random = new Random();     
  	    StringBuffer sb = new StringBuffer();     
  	    for (int i = 0; i < length; i++) {     
  	        int number = random.nextInt(base.length());     
  	        sb.append(base.charAt(number));     
  	    }     
  	    return sb.toString();     
    }*/
    public long crc(String content) {
        if (content == null || content.equals("")) {
            return 0;
        }
        CRC32 crc = new CRC32();
        crc.update(content.getBytes(Charset.forName(Base64Helper.UTF_8)));
        return crc.getValue();
    }

    /**
     * 执行md5签名
     *
     * @param value
     * @return
     */
    public String md5Enocde(String value, String split) {
        if (value == null || value.equals("")) return value;
        return Base64Helper.encode(value).trim() + (split == null ? "" : split) + DigestUtils.md5Hex(value + getMd5SignKey(value));
    }

    private String getMd5SignKey(String content) {
        List<String> keys = getAllKeys();
        long num = crc(content);
        return keys.get(Long.valueOf(num % keys.size()).intValue());
    }

    private List<String> keys;

    private List<String> getAllKeys() {
        if (keys == null) {
            try {
                String str = IOUtils.toString(this.getClass().getResourceAsStream("/md5key.txt"), Base64Helper.UTF_8);
                keys = new ArrayList<String>();
                for (String s : str.split("\n")) {
                    if (s.trim().equals(""))
                        continue;
                    keys.add(s.trim());
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return keys;
    }

    /**
     * 执行MD5验签并返回数据
     *
     * @param signValue
     * @param split
     * @return
     */
    public String md5Decode(String signValue, String split) {
        if (signValue == null || signValue.equals("")) return signValue;
        if (split == null || split.equals("")) {
            throw new RuntimeException("必需提供有效的签名分隔串");
        }
        if (signValue.indexOf(split) < 0) {
            throw new RuntimeException("不是有效的签名数据");
        }
        if (signValue.startsWith("[")) {
            signValue = signValue.substring(1);
        }
        if (signValue.endsWith("]")) {
            signValue = signValue.substring(0, signValue.length() - 1);
        }
        if (signValue.startsWith("\"")) {
            signValue = signValue.substring(1);
        }
        if (signValue.endsWith("\"")) {
            signValue = signValue.substring(0, signValue.length() - 1);
        }
        String value = signValue.substring(0, signValue.lastIndexOf(split));
        value = Base64Helper.decode(value);
        String svalue = signValue.substring(signValue.lastIndexOf(split) + split.length());
        if (svalue.equals(DigestUtils.md5Hex(value + getMd5SignKey(value)))) {
            return value;
        }
        throw new RuntimeException("验证签名数据有效性失败");
    }

    /**
     * 执行DESC加密
     *
     * @param value
     * @return
     */
    public String descEncode(String value) {
        if (value == null || value.equals("")) return value;
        try {
            long num = crc(value);
            return DESEncrypt.toHexString(DESEncrypt.encrypt(getDescKey(num), value)) + "$" + num;
        } catch (Exception e) {
            throw new RuntimeException("执行数据加密失败", e);
        }
    }

    /**
     * desc解密
     *
     * @param value
     * @return
     */
    public String descDecode(String value) {
        if (value == null || value.equals("")) return value;
        try {
            String key = null;
            if (value.lastIndexOf("$") > 0) {
                key = getDescKey(Long.valueOf(value.substring(value.lastIndexOf("$") + 1)));
                value = value.substring(0, value.lastIndexOf("$"));
            } else {
                throw new RuntimeException("无效加密数据");
            }
            return DESEncrypt.decrypt(key, value);
        } catch (Exception e) {
            throw new RuntimeException("执行数据解密失败", e);
        }
    }

    private String getDescKey(long num) {
        List<String> keys = getAllKeys();
        String key = null;
        key = keys.get(Long.valueOf(num % keys.size()).intValue());
        return key.substring(0, 8);
    }

    /**
     * 使用公钥，执行RSA加密
     *
     * @param value
     * @return
     */
    public String rsaEncode(String value) {
        return value;
    }

    /**
     * 使用私钥执行，RSA解密
     *
     * @param value
     * @return
     */
    public String rsaDecode(String value) {
        return value;
    }

    public String md5Enocde(String value) {
        return md5Enocde(value, "_");
    }

    public String md5Decode(String value) {
        return md5Decode(value, "_");
    }
}
