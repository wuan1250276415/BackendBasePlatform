package pro.wuan.privilege.vo;

import lombok.Data;

import java.util.List;

/**
 * 资源树形VO
 *
 * @author: liumin
 * @date: 2021/9/7 15:58
 */
@Data
public class ResourceTreeVO {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型。枚举：menu-菜单，function-功能，list-列表，data-数据
     */
    private String type;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 下级资源树形VO
     */
    private List<ResourceTreeVO> children;

}
