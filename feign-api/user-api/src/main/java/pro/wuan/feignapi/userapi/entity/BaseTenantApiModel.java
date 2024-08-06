package pro.wuan.feignapi.userapi.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import pro.wuan.common.core.annotation.ConvertMapping;
import pro.wuan.common.core.annotation.IDToString;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.feignapi.userapi.feign.OrganFeignClient;
import pro.wuan.feignapi.userapi.feign.UserFeignClient;

import java.util.Date;

@Getter
@Setter
public class BaseTenantApiModel extends IDModel {

    /**
     * 创建人ID
     * @ignore
     */
    @IDToString(type = OrgUserModel.class, feignService = UserFeignClient.class, mappings = {@ConvertMapping(sourceProperty = "userName", targetProperty = "createUserName")})
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    /**
     * 创建人姓名
     */
    @TableField(exist = false)
    private String createUserName;

    /**
     * 创建时间
     * @ignore
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT)
    private Date createTime;

    /**
     * 最后更新人ID
     * @ignore
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Long updateUserId;

    /**
     * 最后更新时间
     * @ignore
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(fill = FieldFill.INSERT_UPDATE)
    private Date updateTime;

    /**
     * 租户ID
     * @ignore
     */
    @IDToString(type = OrgOrganModel.class, feignService = OrganFeignClient.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "tenantName")})
    @TableField(fill = FieldFill.INSERT)
    private Long tenantId;//租户id

    /**
     * 租户名称
     * @ignore
     */
    @TableField(exist = false)
    private String tenantName;

    /**
     * 删除标识
     * @ignore
     */
    @JSONField(serialize = false)
    @JsonIgnore
    @TableLogic(value = "false",delval = "true")
    private boolean deleteFlag;

}
