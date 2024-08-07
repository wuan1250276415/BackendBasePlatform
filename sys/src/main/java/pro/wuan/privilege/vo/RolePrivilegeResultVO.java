package pro.wuan.privilege.vo;

import lombok.Data;

import java.util.List;

/**
 * 角色权限返回结果VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class RolePrivilegeResultVO {

    /**
     * 权限树型结构
     */
    private List<ResourceTreeVO> treeVoList;

    /**
     * 选中权限id列表
     */
    private List<Long> selectedIdList;

}
