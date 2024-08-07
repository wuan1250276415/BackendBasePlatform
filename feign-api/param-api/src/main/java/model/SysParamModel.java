package model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import pro.wuan.common.core.annotation.ConvertMapping;
import pro.wuan.common.core.annotation.IDToString;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.model.BaseDeleteModel;
import pro.wuan.feignapi.appapi.OrgAppModel;

import java.util.Map;


/**
 * 系统参数
 *
 * @author HawkWang
 * @program: tellhowcloud
 * @create 2021-09-06 17:20:20
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("sys_param")
public class SysParamModel extends BaseDeleteModel {
    private static final long serialVersionUID = 1L;

    /**
     * 应用编码
     * @ignore
     */
    @IDToString(type = OrgAppModel.class, uniqueKey = "code", mappings = {@ConvertMapping(sourceProperty = "name",targetProperty = "appName")})
    @TableField(value = "app_code")
    private String appCode;

    /**
     *
     * 应用名称,可能会因为appCode不一样而有问题，当属性不存在时，使用appCode显示
     * @ignore
     */
    @TableField(exist = false)
    private String appName;

    /**
     * 参数唯一标识，包名+类名+参数名
     * @ignore
     */
    @TableField(value = "param_unique")
    private String paramUnique;

    /**
     * 页面显示名称
     * @ignore
     */
    @TableField(value = "param_name")
    private String paramName;

    /**
     * 页面显示类型,radio,text,num
     * @ignore
     */
    @TableField(value = "param_type")
    private String paramType;

    /**
     * 包名+类名
     * @ignore
     */
    @TableField(value = "param_class")
    private String paramClass;

    /**
     * 属性名
     * @ignore
     */
    @TableField(value = "param_code")
    private String paramCode;

    /**
     * 默认值
     * @ignore
     */
    @TableField(value = "param_default")
    private String paramDefault;

    /**
     * 实际值
     */
    @Length(max = 2000, message = "参数值内容长度在0到2000之间", groups = ValidatorUpdateCheck.class)
    @TableField(value = "param_value")
    private String paramValue;

    /**
     *
     * select或者radio的可选值，是一个Json数组的字符串
     * @ignore
     */
    @TableField(value = "param_select")
    private String paramSelect;

    /**
     * 无效状态，0有效，1无效
     */
    @JsonIgnore
    @TableField(value = "status")
    private Integer status;

    /**
     * 选项数组
     */
    @JsonIgnore
    @TableField(exist = false)
    private Map<String,String> paramOptions;

}
