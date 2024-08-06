package pro.wuan.common.auth.util;


import cn.hutool.core.codec.Base64Decoder;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.Key;
import java.security.KeyFactory;
import java.security.spec.X509EncodedKeySpec;
import java.util.Scanner;

public class RSA {


    // 用公钥解密
    public static byte[] decryptByPublicKey(byte[] data, String key) throws Exception {
        // 对密钥解密
        byte[] keyBytes = decryptBASE64(key);
        // 取得公钥
        X509EncodedKeySpec x509KeySpec = new X509EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        Key publicKey = keyFactory.generatePublic(x509KeySpec);
        // 对数据解密
        Cipher cipher = Cipher.getInstance(keyFactory.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * BASE64解密
     *
     * @param key
     * @return
     * @throws Exception
     */
    public static byte[] decryptBASE64(String key) throws Exception {
        return Base64Decoder.decode(key);
    }

    /**
     * description 获取CPU序列号
     *
     * @return java.lang.String
     * @version 1.0
     * @date 2021/1/19 10:33
     */
    public static String getCpuId() throws IOException, InterruptedException {

        // linux，windows命令
        String[] linux = {"dmidecode", "-t", "processor", "|", "grep", "'ID'"};
        String[] windows = {"wmic", "cpu", "get", "ProcessorId"};

        // 获取系统信息
        String property = System.getProperty("os.name");
        if (property.contains("Window")) {
            Process process = Runtime.getRuntime().exec(windows);
            process.getOutputStream().close();
            Scanner sc = new Scanner(process.getInputStream(), "utf-8");
            sc.next();
            return sc.next();
        } else {
            return getCPUID_linux();
        }
    }

    /**
     * 获取CPU序列号 linux
     *
     * @return
     */
    public static String getCPUID_linux() throws InterruptedException {
        String result = "";
        String CPU_ID_CMD = "dmidecode -t 4 | grep ID |sort -u |awk -F': ' '{print $2}'";
        BufferedReader bufferedReader = null;
        Process p = null;
        try {
            p = Runtime.getRuntime().exec(new String[]{"sh", "-c", CPU_ID_CMD});// 管道
            bufferedReader = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            while ((line = bufferedReader.readLine()) != null) {
                result += line;
            }
            System.out.print("result:" + result);
            return result.replaceAll(" ", "").trim();
        } catch (IOException e) {
            System.out.print("获取mac信息错误");
        }
        return result.trim();
    }
}
