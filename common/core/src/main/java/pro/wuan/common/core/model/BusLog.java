package pro.wuan.common.core.model;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @description:
 * @author: oldone
 * @date: 2021/8/26 16:51
 */
@EqualsAndHashCode(callSuper = true)
@Data
@TableName("t_bus_log")
public class BusLog extends BaseDeleteModel {
    /**
     * 应用名
     */
    private String applicationName;
    /**
     * 类名
     */
    private String className;
    /**
     * 方法名
     */
    private String methodName;
    /**
     * 操作信息
     */
    private String operation;


    /**
     * 是否成功  Y是  N否
     */
    private String logSuccess;

    /**
     * 参数
     */
    private String parameters;

    /**
     * ip
     */
    private String ip;

    /**
     * 异常信息
     */
    private String exceptionDetail;

    /**
     * 响应时间
     */
    private Long responseTime;

    /**
     * 请求路径
     */
    private String requestUrl;

    /**
     * 租户id
     */
    private Long tenantId;

    /**
     * 创建用户名称，登入人，操作人
     */
    private String createUserName;

    /**
     * 请求来源
     */
    private String userAgent;

    /**
     * 操作类型
     */
    private String operationType;
}
