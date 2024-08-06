package pro.wuan.feignapi.dictapi.constant;

/**
 * 发送方式
 *
 * @author: oldone
 * @date: 2021/9/22 21:01
 */
public enum SendWayEnum {

    /**
     * web发送
     */
    WEB("WEB"),
    /**
     * 短信
     */
    MESSAGE("MESSAGE"),
    /**
     * 邮件
     */
    EMAIL("EMAIL");


    private String code;

    SendWayEnum(String code) {
        this.code = code;
    }

    //根据uri获取枚举值
    public static SendWayEnum getSendWay(String code) {
        SendWayEnum[] types = values();
        for (SendWayEnum typeEnum : types) {
            if (typeEnum.code.equals(code)) {
                return typeEnum;
            }
        }
        return null;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
