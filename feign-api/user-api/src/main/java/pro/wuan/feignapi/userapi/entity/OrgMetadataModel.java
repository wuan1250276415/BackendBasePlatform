package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.IDModel;

/**
 * 元数据
 *
 * @author: liumin
 * @date: 2021-08-30 11:25:28
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = false)
@Builder
@TableName("org_metadata")
@ServiceType(serviceName = "orgMetadataService")
public class OrgMetadataModel extends IDModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 名称
	 */
	@TableField(value = "name")
	private String name;

	/**
	 * 实体名
	 */
	@TableField(value = "model_class")
	private String modelClass;

	/**
	 * 应用编码
	 */
	@TableField(value = "app_code")
	private String appCode;

}
