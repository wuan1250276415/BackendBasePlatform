package pro.wuan.common.mq.constant;

/**
 * MQ分组、主题枚举
 *
 * @author: oldone
 * @date: 2021/9/15 18:46
 */
public enum GroupEnum {
    /**
     * 日志
     */
    LOGGROUP("loggroup", "test;sys;oss;camunda;bus", TagEnum.LOG.getTag()),
    /**
     * 流程注册应用
     */
    CAMUNDAREGISTRATION(TagEnum.ACT_APPLICATION_REGISTRATION.getTag(), "test;sys;oss", TagEnum.ACT_APPLICATION_REGISTRATION.getTag()),
    CAMUNDAREGISTRATIONBUS(TagEnum.ACT_BUSINESS_REGISTRATION.getTag(), "test;sys;oss", TagEnum.ACT_BUSINESS_REGISTRATION.getTag()),
    /**
     * 流程配置
     */
    CAMUNDAACTSTART(TagEnum.CAMUNDA_ACT_START.getTag(), "camunda", TagEnum.CAMUNDA_ACT_START.getTag()),
    CAMUNDAACTEND(TagEnum.CAMUNDA_ACT_END.getTag(), "camunda", TagEnum.CAMUNDA_ACT_END.getTag()),
    CAMUNDAACTSTATUEEND(TagEnum.CAMUNDA_ACT_STATUS_END.getTag(), "camunda", TagEnum.CAMUNDA_ACT_STATUS_END.getTag()),
    /**
     * sys-bus  系统模块到消息中心
     */
    SYSTOBUSGROUP("systobusroup", "sys", TagEnum.UN_KNOW.getTag()),
    /**
     * sys-camunda  系统模块到工作流
     */
    SYSTOCAMUNDAGROUP("systocamundagroup", "sys", TagEnum.UN_KNOW.getTag()),
    /**
     * oss-bus
     */
    OSSTOBUSGROUP("osstobusroup", "oss", TagEnum.UN_KNOW.getTag()),
    /**
     * camunda-bus
     */
    CAMUNDATOBUSGROUP("camundatobusgroup", "camunda", TagEnum.NOTICE.getTag()),
    /**
     * test-bus
     */
    TESTTOBUSGROUP("testtobusgroup", "test", TagEnum.NOTICE.getTag()),
    /**
     * 未知分组
     */
    UNKNOWGROUP("unkouw", "unkouw", TagEnum.UN_KNOW.getTag());

    //消息分组
    private String group;
    //消息主题
    private String topic;
    //消费者监听消费类型
    private String tag;

    GroupEnum(String group, String topic, String tag) {
        this.group = group;
        this.topic = topic;
        this.tag = tag;
    }

    public static GroupEnum buildEnum(String group, String topic, String tag) {
        GroupEnum.UNKNOWGROUP.setGroup(group);
        GroupEnum.UNKNOWGROUP.setTopic(topic);
        GroupEnum.UNKNOWGROUP.setTag(tag);
        return GroupEnum.UNKNOWGROUP;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getTopic() {
        return topic;
    }

    public void setTopic(String topic) {
        this.topic = topic;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public static GroupEnum getCamundaeActStart(String group) {
        String newGroup = GroupEnum.CAMUNDAACTSTART.getGroup() + "_" + group;
        GroupEnum.CAMUNDAACTSTART.setGroup(newGroup);
        return GroupEnum.CAMUNDAACTSTART;
    }

    public static GroupEnum getCamundaActEnd(String group) {
        String newGroup = GroupEnum.CAMUNDAACTEND.getGroup() + "_" + group;
        GroupEnum.CAMUNDAACTEND.setGroup(newGroup);
        return GroupEnum.CAMUNDAACTEND;
    }

    public static GroupEnum getCamundaActStatusEnd(String group) {
        String newGroup = GroupEnum.CAMUNDAACTSTATUEEND.getGroup() + "_" + group;
        GroupEnum.CAMUNDAACTSTATUEEND.setGroup(newGroup);
        return GroupEnum.CAMUNDAACTSTATUEEND;
    }
}
