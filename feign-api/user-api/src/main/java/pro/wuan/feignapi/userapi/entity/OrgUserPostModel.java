package pro.wuan.feignapi.userapi.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;
import pro.wuan.common.core.annotation.ServiceType;
import pro.wuan.common.core.model.IDModel;

/**
 * 用户岗位关联表
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
@TableName("org_user_post")
@ServiceType(serviceName = "orgUserPostService")
public class OrgUserPostModel extends IDModel {

	private static final long serialVersionUID = 1L;

	/**
	 * 用户id
	 */
	@TableField(value = "user_id")
	private Long userId;

	/**
	 * 岗位id
	 */
	@TableField(value = "post_id")
	private Long postId;

}
