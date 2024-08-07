package pro.wuan.constant;

/**
 * @author: oldone
 * @date: 2021/9/14 10:52
 */
public class DictConstant {
    /**
     * 所有字典缓存key
     */
    public static final String ALL_DICT_KEY = "all_dict";


    /**
     * 人脸识别相关参数
     */
    public static final String RLSB_URL = "http://192.168.45.50:8000/api/v1/recognition";

    public static final String X_API_KEY = "16cd619d-0839-45d8-8f9d-5806ea726177";

    // 验证类型
    public static final String YZLX_MJ = "1";
    public static final String YZLX_ZYRY = "2";

    // 生物识别类型
    public static final String SWSBLX_RL="1"; //人脸
    public static final String SWSBLX_ZW="2"; //指纹

    // 照片序号
    public static final String ZPXH_ZL="0"; // 正脸
    public static final String ZPXH_YCL="1"; // 右侧脸
    public static final String ZPXH_ZCL="2"; // 左侧脸

    //指纹数据格式
    public static final int ZWGS_MB=1; // 模板
    public static final int ZWGS_TZ=2; // 特征


}
