package pro.wuan.feignapi.userapi.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.validator.constraints.Length;
import org.hibernate.validator.constraints.Range;
import org.springframework.format.annotation.DateTimeFormat;
import pro.wuan.common.core.annotation.*;
import pro.wuan.common.core.annotation.constraints.group.ValidatorSaveCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateCheck;
import pro.wuan.common.core.annotation.constraints.group.ValidatorUpdateOneselfCheck;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.BaseTenantModel;
import pro.wuan.feignapi.ossapi.model.ISysAttMainVo;
import pro.wuan.feignapi.ossapi.model.SysAttMainTVo;
import pro.wuan.feignapi.ossapi.model.SysAttMainVo;

import jakarta.validation.constraints.*;

import java.io.Serial;
import java.util.Date;
import java.util.List;

/**
 * 用户
 *
 * @author: liumin
 * @date: 2021-08-30 10:40:59
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_user")
@ServiceType(serviceName = "orgUserService")
public class OrgUserModel extends BaseTenantModel implements ISysAttMainVo {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @Length(max = 205, message = "用户名最大长度不能超过205", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "user_name")
    private String userName;

    /**
     * 登录名
     */
    @NotBlank(message = "登录名不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @Length(max = 200, message = "登录名最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "login_name")
    private String loginName;

    /**
     * 密码
     */
    @JSONField(serialize = false)
    @JsonIgnore
    @Length(max = 30, message = "密码最大长度不能超过30", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "password")
    private String password;

    /**
     * 密码盐
     */
    @TableField(value = "salt")
    private String salt;

    /**
     * 类型。枚举：superAdmin-超级管理员，unitAdmin-单位管理员，commonUser-普通用户
     */
    @EnumFiled(type = CommonConstant.USER_TYPE.class, targetProperty = "typeName")
    @TableField(value = "type")
    private String type;

    @TableField(exist = false)
    private String typeName;

    /**
     * 性别。枚举：man-男，woman-女
     */
    @EnumFiled(type = CommonConstant.SEX.class, targetProperty = "sexName")
    @TableField(value = "sex")
    private String sex;

    @TableField(exist = false)
    private String sexName;

    /**
     * 身份证号
     */
    @Pattern(regexp = "^(\\d{6}(18|19|20)\\d{2}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3}(\\d|X|x))|(\\d{8}(0[1-9]|1[012])(0[1-9]|[12]\\d|3[01])\\d{3})$", message = "身份证号 格式有误", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @Size(max = 18, message = "身份证号 最大长度为{max}", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "id_card_no")
    private String idCardNo;

    /**
     * 头像url
     */
    @Length(max = 180, message = "头像url最大长度不能超过180", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "head_image_url")
    private String headImageUrl;

    /**
     * 手机号
     */
    @Length(max = 30, message = "电话最大长度不能超过30", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, ValidatorUpdateOneselfCheck.class})
    @TableField(value = "tel")
    private String tel;


    /**
     * 邮箱
     */
    @Length(max = 30, message = "邮箱最大长度不能超过30", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, ValidatorUpdateOneselfCheck.class})
    @TableField(value = "email")
    private String email;


    /**
     * 昵称
     */
    @Length(max = 200, message = "昵称最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, ValidatorUpdateOneselfCheck.class})
    @TableField(value = "nickname")
    private String nickname;


    /**
     * 个人简介
     */
    @Length(max = 200, message = "个人简介最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, ValidatorUpdateOneselfCheck.class})
    @TableField(value = "personal_profile")
    private String personalProfile;


    /**
     * 国家/地区
     */
    @TableField(value = "country_id")
    private Long countryId;


    /**
     * 省
     */
    @TableField(value = "province_id")
    private Long provinceId;


    /**
     * 市
     */
    @TableField(value = "city_id")
    private Long cityId;


    /**
     * 街道地址
     */
    @Length(max = 200, message = "街道地址最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class, ValidatorUpdateOneselfCheck.class})
    @TableField(value = "address")
    private String address;


    /**
     * 备注
     */
    @Length(max = 200, message = "备注最大长度不能超过200", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @TableField(value = "remark")
    private String remark;

    /**
     * 登陆失败次数
     */
    @TableField(value = "failure_count")
    private Integer failureCount;

    /**
     * 登陆时锁定时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @TableField(value = "lock_time")
    private Date lockTime;

    /**
     * 最后修改密码时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @TableField(value = "modify_password_time")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private Date modifyPasswordTime;

    /**
     * 直接上级id
     */
    @TableField(value = "leader_user_id")
    private Long leaderUserId;

    /**
     * 组织机构id
     */
    @NotNull(message = "所属部门不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @IDToString(type = OrgOrganModel.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "organName")})
    @TableField(value = "organ_id")
    private Long organId;

    /**
     * 组织机构名称
     */
    @TableField(exist = false)
    private String organName;

    /**
     * 启用状态。枚举：0-停用，1-启用
     */
    @NotNull(message = "启用状态不能为空", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @Range(max = 1, message = "启用状态最大值不能超过1", groups = {ValidatorSaveCheck.class, ValidatorUpdateCheck.class})
    @EnumFiled(type = CommonConstant.USE_STATUS.class, targetProperty = "statusName")
    @TableField(value = "status")
    private Integer status;

    @TableField(exist = false)
    private String statusName;

    /**
     * 所属组织机构
     */
    @TableField(exist = false)
    private OrgOrganModel orgOrganModel;

    /**
     * 岗位列表
     */
    @TableField(exist = false)
    private List<OrgPostModel> orgPostModels;

    /**
     * 用户岗位关联列表
     */
    @ListToList(fk = "userId", fkType = OrgUserPostModel.class)
    @ListToString(fk = "userId", fkType = OrgUserPostModel.class, subFk = "postId", subType = OrgPostModel.class,
            mappings = {
                    @ConvertMapping(sourceProperty = "id", targetProperty = "orgUserPostModelIds"),
                    @ConvertMapping(sourceProperty = "name", targetProperty = "orgUserPostModelNames")
            })
    @TableField(exist = false)
    private List<OrgUserPostModel> orgUserPostModels;

    /**
     * 用户所有岗位的Id字符串，分号间隔
     */
    @IdsToList(type = OrgUserPostModel.class, convertProperty = "postId", targetProperty = "orgUserPostModels")
    @TableField(exist = false)
    private String orgUserPostModelIds;

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
     * 用户角色关联列表
     */
    @ListToList(fk = "userId", fkType = OrgUserRoleModel.class)
    @ListToString(fk = "userId", fkType = OrgUserRoleModel.class, subFk = "roleId", subType = OrgRoleModel.class,
            mappings = {
                    @ConvertMapping(sourceProperty = "id", targetProperty = "orgUserRoleModelIds"),
                    @ConvertMapping(sourceProperty = "name", targetProperty = "orgUserRoleModelNames")
            })
    @TableField(exist = false)
    private List<OrgUserRoleModel> orgUserRoleModels;

    /**
     * 用户所有角色的Id字符串，分号间隔
     */
    @IdsToList(type = OrgUserRoleModel.class, convertProperty = "roleId", targetProperty = "orgUserRoleModels")
    @TableField(exist = false)
    private String orgUserRoleModelIds;

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
     * 记录创建人ID
     */
    @IDToString(type = OrgUserModel.class, mappings = {@ConvertMapping(sourceProperty = "userName", targetProperty = "createUserName")})
    @TableField(fill = FieldFill.INSERT)
    private Long createUserId;

    /**
     * 记录创建人真实姓名
     */
    @TableField(exist = false)
    private String createUserName;


    /**
     * 用户锁屏状态 1锁屏  0未锁屏
     */
    @TableField(value = "lock_screen")
    private boolean lockScreen;


    /**
     * 人脸识别ID
     */
    @TableField(value = "image_id")
    private String imageId;

    /**
     * 冗余字段
     */
    @TableField(value = "rock_field")
    private String rockField;

    /**
     * 是否绑定所属组织机构
     */
    @TableField(value = "bound_organ")
    private boolean boundOrgan;

    /**
     * 扩展字符串属性01
     */
    @TableField(value = "attr_string_01")
    private String attrString01;

    /**
     * 扩展字符串属性02
     */
    @TableField(value = "attr_string_02")
    private String attrString02;

    /**
     * 扩展字符串属性03
     */
    @TableField(value = "attr_string_03")
    private String attrString03;

    /**
     * 扩展字符串属性04
     */
    @TableField(value = "attr_string_04")
    private String attrString04;

    /**
     * 扩展字符串属性05
     */
    @TableField(value = "attr_string_05")
    private String attrString05;

    /**
     * 扩展字符串属性06
     */
    @TableField(value = "attr_string_06")
    private String attrString06;

    /**
     * 扩展字符串属性07
     */
    @TableField(value = "attr_string_07")
    private String attrString07;

    /**
     * 扩展字符串属性08
     */
    @TableField(value = "attr_string_08")
    private String attrString08;

    /**
     * 扩展字符串属性09
     */
    @TableField(value = "attr_string_09")
    private String attrString09;

    /**
     * 扩展字符串属性10
     */
    @TableField(value = "attr_string_10")
    private String attrString10;

    /**
     * 扩展整型属性01
     */
    @TableField(value = "attr_bigint_01")
    private Long attrBigint01;

    /**
     * 扩展整型属性02
     */
    @TableField(value = "attr_bigint_02")
    private Long attrBigint02;

    /**
     * 扩展整型属性03
     */
    @TableField(value = "attr_bigint_03")
    private Long attrBigint03;

    /**
     * 扩展整型属性04
     */
    @TableField(value = "attr_bigint_04")
    private Long attrBigint04;

    /**
     * 扩展整型属性05
     */
    @TableField(value = "attr_bigint_05")
    private Long attrBigint05;

    /**
     * 扩展整型属性06
     */
    @TableField(value = "attr_bigint_06")
    private Long attrBigint06;

    /**
     * 扩展整型属性07
     */
    @TableField(value = "attr_bigint_07")
    private Long attrBigint07;

    /**
     * 扩展整型属性08
     */
    @TableField(value = "attr_bigint_08")
    private Long attrBigint08;

    /**
     * 扩展整型属性09
     */
    @TableField(value = "attr_bigint_09")
    private Long attrBigint09;

    /**
     * 扩展整型属性10
     */
    @TableField(value = "attr_bigint_10")
    private Long attrBigint10;

    @TableField(exist = false)
    private SysAttMainVo sysAttMainVo;// 定义附件的接收参数

    // 定义附件的接收参数
    @TableField(exist = false)
    private SysAttMainTVo sjzxVo;

    /**
     * 特征值1
     */
    @TableField(value = "feature1")
    private String feature1;
    /**
     * 特征值2
     */
    @TableField(value = "feature2")
    private String feature2;

    public static final String USERNAME = "userName";
}
