package pro.wuan.privilege.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色权限查询VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class RolePrivilegeQueryVO {

    /**
     * 角色id
     */
    @NotNull(message = "roleId不能为空")
    private Long roleId;

    /**
     * 可授权菜单id列表
     */
    private List<Long> menuIdList;

}
