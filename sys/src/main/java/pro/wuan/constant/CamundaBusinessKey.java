package pro.wuan.constant;

public enum CamundaBusinessKey {

    CAMUNDATEST("businessKey","测试流程");

    private String type;

    private String desc;

    CamundaBusinessKey(String type, String desc) {
        this.type = type;
        this.desc = desc;
    }

    public String getType() {
        return type;
    }

    public String getDesc() {
        return desc;
    }

}
