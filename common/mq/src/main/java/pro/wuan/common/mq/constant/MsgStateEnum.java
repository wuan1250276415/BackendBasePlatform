package pro.wuan.common.mq.constant;

/**
 * 消息状态枚举
 *
 * @author: oldone
 * @date: 2021/9/17 23:39
 */
public enum MsgStateEnum {
    /**
     * 发送中
     */
    SENDING("0"),
    /**
     * 发送成功
     */
    SUCCESS("1"),
    /**
     * 发送失败
     */
    FAILURE("2");


    private String state;

    MsgStateEnum(String state) {
        this.state = state;
    }


    public static MsgStateEnum getMsgStateEnum(String state) {
        MsgStateEnum[] msgStateEnums = values();
        for (MsgStateEnum stateEnum : msgStateEnums) {
            if (stateEnum.state.equals(state)) {
                return stateEnum;
            }
        }
        return null;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
