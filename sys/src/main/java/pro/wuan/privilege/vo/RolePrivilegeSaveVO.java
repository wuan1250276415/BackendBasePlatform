package pro.wuan.privilege.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * 角色权限保存VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class RolePrivilegeSaveVO {

    /**
     * 角色id
     */
    @NotNull(message = "roleId不能为空")
    private Long roleId;

    /**
     * 菜单权限id列表
     */
    private List<Long> menuIdList;

    /**
     * 功能权限id列表（按钮）
     */
    private List<Long> functionIdList;

    /**
     * 列表权限id列表
     */
    private List<Long> listIdList;

    /**
     * 数据权限id列表
     */
    private List<Long> dataIdList;

}
