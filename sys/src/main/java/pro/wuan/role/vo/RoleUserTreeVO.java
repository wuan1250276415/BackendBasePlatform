package pro.wuan.role.vo;

import lombok.Data;

import java.util.List;

/**
 * 角色关联用户树形VO
 *
 * @author: liumin
 * @date: 2021/9/7 15:58
 */
@Data
public class RoleUserTreeVO {

    /**
     * 结点id
     */
    private Long nodeId;

    /**
     * 结点名称
     */
    private String nodeName;

    /**
     * 是否选中
     */
    private boolean selected;

    /**
     * 下级列表
     */
    private List<RoleUserTreeVO> children;

}
