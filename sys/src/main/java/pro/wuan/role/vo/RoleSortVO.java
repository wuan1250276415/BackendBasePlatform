package pro.wuan.role.vo;

import lombok.Data;
import pro.wuan.feignapi.userapi.entity.OrgRoleModel;

import java.util.List;

/**
 * 角色分类VO
 *
 * @author: liumin
 * @date: 2021/9/7 15:58
 */
@Data
public class RoleSortVO {

    /**
     * 分类id
     */
    private Long id;

    /**
     * 分类名称
     */
    private String name;

    /**
     * 角色列表
     */
    private List<OrgRoleModel> roleList;

}
