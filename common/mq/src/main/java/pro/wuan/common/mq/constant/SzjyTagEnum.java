package pro.wuan.common.mq.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: tag枚举--用以标识消息的类型
 * @author: libra
 * @date: 2023/2/9 11:05
 */
public enum SzjyTagEnum {
    /**
     * appDataEX心跳数据
     */
    APP_DATAEX_HEART_BEAT_MSG("heartBeatMsg"),
    /**
     * appDataEX应用注册
     */
    APP_DATAEX_REGISTRATION("register"),
    /**
     * appDataEX基础数据同步
     */
    APP_DATAEX_BASE_DATA("basedata"),
    /**
     * appDataEX消息回复
     */
    APP_DATAEX_MSG_REPLY("reply"),
    /**
     * appDataEX新增
     */
    APP_DATAEX_ADD("add"),
    /**
     * appDataEX修改
     */
    APP_DATAEX_UPDATE("update"),
    /**
     * appDataEX删除
     */
    APP_DATAEX_DELETE("delete"),
    /**
     * 公共
     */
    APP_DATAEX_COMMON("common");

    private String tag;

    SzjyTagEnum(String tag) {
        this.tag = tag;
    }


    public static SzjyTagEnum getTagEnum(String tag) {
        SzjyTagEnum[] tagEnums = values();
        for (SzjyTagEnum tagEnum : tagEnums) {
            if (tagEnum.tag.equals(tag)) {
                return tagEnum;
            }
        }
        return null;
    }

    public static String getTags() {
        List<String> tagList = Arrays.stream(values()).map(y -> y.tag).collect(Collectors.toList());
        return StringUtils.join(tagList, "||");
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

}
