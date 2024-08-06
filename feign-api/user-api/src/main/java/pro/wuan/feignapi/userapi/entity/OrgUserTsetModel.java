package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;
import pro.wuan.common.core.model.BaseTenantModel;

import java.util.List;

/**
 * 用户
 *
 * @author: libra
 * @date: 2021-08-30 10:40:59
 */
@Data
public class OrgUserTsetModel extends BaseTenantModel {

    /**
     * 用户名
     */
    private String userName;

    /**
     * 登录名
     */
    private String loginName;

    /**
     * 密码
     */
    private String password;

    /**
     * 密码盐
     */
    @TableField(value = "salt")
    private String salt;

    /**
     * 类型。枚举：superAdmin-超级管理员，unitAdmin-单位管理员，commonUser-普通用户
     */
    private String type;

    /**
     * 手机号
     */
    private String tel;

    /**
     * 邮箱
     */
    private String email;


    /**
     * 昵称
     */
    private String nickname;


    /**
     * 个人简介
     */
    private String personalProfile;


    /**
     * 国家/地区
     */
    private Long countryId;



    /**
     * 省
     */
    private Long provinceId;



    /**
     * 市
     */
    private Long cityId;


    /**
     * 街道地址
     */
    private String  address;


    /**
     * 备注
     */
    private String remark;

    /**
     * 登陆失败次数
     */
    private Integer failureCount;

    /**
     * 直接上级id
     */
    private Long leaderUserId;

    /**
     * 组织机构id
     */
    private Long organId;

    /**
     * 组织机构名称
     */
    @TableField(exist = false)
    private String organName;

    /**
     * 启用状态。枚举：0-停用，1-启用
     */
    private Integer status;

    @TableField(exist = false)
    private String statusName;

    /**
     * 所属组织机构
     */
    @TableField(exist = false)
    private OrgOrganModel orgOrganModel;


    /**
     * 用户所有岗位的名称字符串，分号间隔
     */
    @TableField(exist = false)
    private String orgUserPostModelNames;

    /**
     * 用户岗位id列表
     */
    @TableField(exist = false)
    private List<Long> postIdList;

    /**
     * 用户所有角色的名称字符串，分号间隔
     */
    @TableField(exist = false)
    private String orgUserRoleModelNames;

    /**
     * 用户角色id列表
     */
    @TableField(exist = false)
    private List<Long> roleIdList;

    /**
     * 注册的报错
     */
    @TableField(exist = false)
    private String error;

    /**
     * 身份证号
     */
    private String idCardNo;

    /**
     * 性别
     */
    private String sex;

    /**
     * 头像url
     */
    private String headImageUrl;

}
