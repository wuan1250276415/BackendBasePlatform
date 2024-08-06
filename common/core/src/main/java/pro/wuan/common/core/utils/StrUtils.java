package pro.wuan.common.core.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * 字符串工具类
 */
public class StrUtils {

	public static String trim(String str){
		if(StrUtils.isBlank(str)){
			return "";
		}
		return str.trim();
	}
	
	/**
	 * 判断是否是空字符串 null和"" 都返回 true
	 * 
	 * @author Robin Chang
	 * @param s
	 * @return
	 */
	public static boolean isEmpty(String s) {
		if (s != null && !s.equals("")) {
			return false;
		}
		return true;
	}
	
	/**
	 * @description 判断字符串是否包含具体内容
	 * @param s
	 * @return
	 */
	public static boolean isNotEmpty(String s) {
		return !isEmpty(s);
	}

    /**
     * 是否为空字符串
     * @param str
     * @return
     */
    public static boolean isBlank(String str) {
        if (str == null) {
            return true;
        } else {
            int len = str.length();
            if (len == 0) {
                return true;
            } else {
                int i = 0;

                while(i < len) {
                    switch(str.charAt(i)) {
                        case '\t':
                        case '\n':
                        case '\r':
                        case ' ':
                            ++i;
                            break;
                        default:
                            return false;
                    }
                }

                return true;
            }
        }
    }

    /**
     * 是否为非空字符串
     * @param str
     * @return
     */
    public static boolean notBlank(String str) {
        return !isBlank(str);
    }

    /**
     * 对字符串进行URLEncoder编码，默认utf-8字符编码
     * @param str
     * @return
     */
    public static String encode(String str){
        return encode(str, "utf-8");
    }

    /**
     * 对字符串进行指定字符格式URLEncoder编码
     * @param str
     * @param enc
     * @return
     */
    public static String encode(String str, String enc){
        try {
            return URLEncoder.encode(str, enc);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 对字符串进行URLDecoder解码，默认utf-8字符编码
     * @param str
     * @return
     */
    public static String decode(String str){
        return decode(str, "utf-8");
    }

    /**
     * 对字符串进行指定字符格式URLDecoder解码
     * @param str
     * @param enc
     * @return
     */
    public static String decode(String str, String enc){
        try {
            return URLDecoder.decode(str, enc);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }
    

    
}
