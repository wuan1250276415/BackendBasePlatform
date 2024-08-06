package pro.wuan.common.mq.constant;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @description: tag枚举--用以标识消息的类型
 * @author: oldone
 * @date: 2020/9/9 11:05
 */
public enum TagEnum {

    /**
     * 日志
     */
    LOG("log"),
    /**
     * camunda流程
     */
    CAMUNDA("camunda"),
    /**
     * camunda应用注册
     */
    ACT_APPLICATION_REGISTRATION("act_application_registration"),
    /**
     * camunda业务注册
     */
    ACT_BUSINESS_REGISTRATION("act_business_registration"),
    /**
     * camunda开始监听
     */
    CAMUNDA_ACT_START("camunda_act_start"),
    /**
     * camunda结束监听
     */
    CAMUNDA_ACT_END("camunda_act_end"),
    /**
     * camunda结束修改业务状态
     */
    CAMUNDA_ACT_STATUS_END("camunda_act_status_end"),
    /**
     * 通知
     */
    NOTICE("notice"),
    /**
     * 待办
     */
    PENDING("pending"),

    /**
     * 消息发送
     */
    MESSAGE_CENTER("message_center"),

    /**
     * 消息中心发送状态通知
     */
    MESSAGE_CENTER_STATUS("message_center_status"),
    /**
     * 未知
     */
    UN_KNOW("un_know");

    private String tag;

    TagEnum(String tag) {
        this.tag = tag;
    }


    public static TagEnum getTagEnum(String tag) {
        TagEnum[] tagEnums = values();
        for (TagEnum tagEnum : tagEnums) {
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
