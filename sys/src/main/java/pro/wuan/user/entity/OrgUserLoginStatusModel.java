package pro.wuan.user.entity;

import com.alibaba.fastjson.annotation.JSONField;
import com.alibaba.fastjson.serializer.ToStringSerializer;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import pro.wuan.common.core.annotation.ConvertMapping;
import pro.wuan.common.core.annotation.DicFiled;
import pro.wuan.common.core.annotation.IDToString;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.constant.CommonConstant;
import pro.wuan.common.core.model.IDModel;
import pro.wuan.feignapi.userapi.entity.OrgOrganModel;
import pro.wuan.user.service.IOrgUserLoginStatusService;

import java.util.Date;

/**
 * 在线用户
 *
 * @program: tellhowcloud
 * @author huangtianji
 * @create 2022-10-11 09:35:17
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_user_login_status")
@ServiceType(service = IOrgUserLoginStatusService.class)
public class OrgUserLoginStatusModel extends IDModel {
	private static final long serialVersionUID = 1L;

	/**
	 * 用户Id
	 */
	@JSONField(serializeUsing = ToStringSerializer.class)
	@TableField(value = "user_id")
	private Long userId;

	/**
	 * 用户姓名
	 */
	@TableField(value = "user_name")
	private String userName;

	/**
	 * 登录名
	 */
	@TableField(value = "login_name")
	private String loginName;

	/**
	 * 登录IP
	 */
	@TableField(value = "login_ip")
	private String loginIp;

	/**
	 * 登录地址
	 */
	@TableField(value = "login_address")
	private String loginAddress;

	/**
	 * 登录浏览器
	 */
	@TableField(value = "login_browser")
	private String loginBrowser;

	/**
	 * 登录操作系统
	 */
	@TableField(value = "login_os")
	private String loginOs;

	/**
	 * 登录状态
	 */
	@DicFiled(key = "YHZX",name = "登录状态",targetProperty = "loginStatusName")
	@TableField(value = "login_status")
	private String loginStatus;

	/**
	 * 登录时间
	 */
	@JsonFormat(timezone="GMT+8",pattern = CommonConstant.DATETIME_FORMAT2)
	@DateTimeFormat(pattern = CommonConstant.DATETIME_FORMAT2)
	@JSONField(format= CommonConstant.DATETIME_FORMAT2)
	@TableField(value = "login_time")
	private Date loginTime;

	/**
	 * @ignore
	 * 租户ID
	 */
	@IDToString(type = OrgOrganModel.class, mappings = {@ConvertMapping(sourceProperty = "name", targetProperty = "tenantName")})
	@TableField(value = "tenant_id")
	private Long tenantId;//租户id

	/**
	 * 登录状态名称
	 * */
	@TableField(exist = false)
	private String loginStatusName;

	/**
	 * 单位名称
	 * */
	@TableField(exist = false)
	private String tenantName;


}
