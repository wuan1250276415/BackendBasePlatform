package pro.wuan.gateway.config;

/**
 * @author: oldone
 * @date: 2022/9/6 15:11
 */
public class NacosConfigInfo {
    public NacosConfigInfo(String serverAddr, String namespace, String group, String dataId, boolean refresh, Class cls) {
        this.serverAddr = serverAddr;
        this.namespace = namespace;
        this.group = group;
        this.dataId = dataId;
        this.refresh = refresh;
        this.cls = cls;
    }

    public NacosConfigInfo() {
    }

    private String serverAddr;
    private String namespace;
    /**
     * 获取nacos groupId
     * <p>默认NacosConstant.GROUP_ID</p>
     *
     * @return nacos groupId
     */
    private String group;
    /**
     * 获取nacos dataId
     *
     * @return nacos dataId
     */
    private String dataId;
    private boolean refresh = true;
    private Class cls = String.class;

    public String getServerAddr() {
        return serverAddr;
    }

    public void setServerAddr(String serverAddr) {
        this.serverAddr = serverAddr;
    }

    public String getNamespace() {
        return namespace;
    }

    public void setNamespace(String namespace) {
        this.namespace = namespace;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public boolean isRefresh() {
        return refresh;
    }

    public void setRefresh(boolean refresh) {
        this.refresh = refresh;
    }

    public Class getCls() {
        return cls;
    }

    public void setCls(Class cls) {
        this.cls = cls;
    }

    public long getTimeout() {
        return 5000L;
    }
}
