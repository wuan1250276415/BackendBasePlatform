package pro.wuan.common.auth.util;


import org.apache.commons.codec.binary.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import java.security.Key;
import java.security.SecureRandom;

/**
 * @author: oldone
 * @date: 2022/8/15 20:40
 */
public class DesUtil {

    private static int parse(char c) {
        if (c >= 'a')
            return (c - 'a' + 10) & 0x0f;
        if (c >= 'A')
            return (c - 'A' + 10) & 0x0f;
        return (c - '0') & 0x0f;
    }

    public static byte[] HexString2Bytes(String hexstr) {
        byte[] b = new byte[hexstr.length() / 2];
        int j = 0;
        for (int i = 0; i < b.length; i++) {
            char c0 = hexstr.charAt(j++);
            char c1 = hexstr.charAt(j++);
            b[i] = (byte) ((parse(c0) << 4) | parse(c1));
        }
        return b;
    }

    public static SecretKey keyGenerator(String keyStr) throws Exception {
        byte input[] = HexString2Bytes(keyStr);
        DESKeySpec desKey = new DESKeySpec(input);
        // 创建一个密匙工厂，然后用它把DESKeySpec转换成
        SecretKeyFactory keyFactory = SecretKeyFactory.getInstance("DES");
        SecretKey securekey = keyFactory.generateSecret(desKey);
        return securekey;
    }


    // 加密
    public static String encrypt(String data, SecretKey key) throws Exception {
        // 实例化Cipher对象，它用于完成实际的加密操作
        Cipher cipher = Cipher.getInstance("DES/ECB/PKCS5Padding");
        SecureRandom random = new SecureRandom();
        // 初始化Cipher对象，设置为加密模式
        cipher.init(Cipher.ENCRYPT_MODE, key, random);
        byte[] results = cipher.doFinal(data.getBytes());
        // 执行加密操作。加密后的结果通常都会用Base64编码进行传输
        return Base64.encodeBase64String(results);
    }

    // 解密
    public static String decrypt(String data, String key) throws Exception {
        Key deskey = keyGenerator(key);
        Cipher cipher = Cipher.getInstance("DES/ECB/NoPadding");
        // 初始化Cipher对象，设置为解密模式
        cipher.init(Cipher.DECRYPT_MODE, deskey);
        // 执行解密操作
        return new String(cipher.doFinal(Base64.decodeBase64(data)));
    }
}
