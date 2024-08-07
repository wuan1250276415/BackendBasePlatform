package pro.wuan.organmodelsetting.model;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.feignapi.userapi.entity.BaseTenantApiModel;
import pro.wuan.organmodelsetting.service.IOrgOrganModelSettingService;


/**
 * 查询模式数据配置
 *
 * @program: tellhowcloud
 * @author libra
 * @create 2024-01-22 11:48:48
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_organ_model_setting")
@ServiceType(service = IOrgOrganModelSettingService.class)
public class OrgOrganModelSettingModel extends BaseTenantApiModel {
	private static final long serialVersionUID = 1L;

	/**
	 * 配置单位id
	 */
	@TableField(value = "organ_ids")
	private String organIds;

	/**
	 * 模式名称
	 */
	@TableField(value = "mode")
	private String mode;


}
