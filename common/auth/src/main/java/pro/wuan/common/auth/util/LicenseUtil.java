package pro.wuan.common.auth.util;

import lombok.extern.slf4j.Slf4j;

/**
 * @author: oldone
 * @date: 2022/9/6 11:18
 */
@Slf4j
public class LicenseUtil {

    /**
     * 校验license合法性
     *
     * @param license
     * @return
     */
    public static String checkLicense(String license) {
        try {
            /*==================开始进行解密=====================*/
            //BASE64解密license
            byte[] debs = RSA.decryptBASE64(license);
            String deLicenseStr = new String(debs);
            String[] contents = deLicenseStr.split("T000T");
            //被DES密钥加密后得内容
            String deEnStr = contents[0];
            //被RSA 私钥加密后得DES密钥
            String deRsaDeskey = contents[1];
            //RSA公钥
            String dePubkey = contents[2];
            //用RSA公钥解密出DES密钥
            byte[] dedebs = RSA.decryptByPublicKey(RSA.decryptBASE64(deRsaDeskey), dePubkey);
            //DES密钥字符串
            String dedekey = new String(dedebs);
            //DES进行内容解密
            String result = DesUtil.decrypt(deEnStr, dedekey);

            String[] strs = result.split("TT");
            String decpuseq = strs[0];
            String detime = strs[1];

            String cpuId = RSA.getCpuId().trim();
            log.info("cpuId:" + cpuId);
            if (!decpuseq.equals(cpuId)) {
                return "机器码不一致，License无效";
            }

//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//            Date deDate = sdf.parse(detime + " 23:59:59");
//            if (deDate.compareTo(new Date()) < 0) {
//                return "本机时间超过License过期时间，License已过期";
//            }
            return "success";
        } catch (Exception e) {
            return "非法License，请检查输入";
        }
    }
}
