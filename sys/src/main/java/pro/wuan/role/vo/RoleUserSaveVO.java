package pro.wuan.role.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色用户关联保存VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class RoleUserSaveVO {

    /**
     * 角色id
     */
    @NotNull(message = "roleId不能为空")
    private Long roleId;

    /**
     * 用户id列表
     */
    private List<Long> userIds;

}
