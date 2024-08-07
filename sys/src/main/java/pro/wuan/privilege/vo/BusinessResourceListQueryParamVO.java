package pro.wuan.privilege.vo;

import lombok.Data;

import java.util.List;

/**
 * 业务资源列表查询参数VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class BusinessResourceListQueryParamVO {

    /**
     * 类型列表
     */
    private List<String> typeList;

    /**
     * 应用id
     */
    private Long appId;

    /**
     * 名称
     */
    private String name;

    /**
     * 显示状态。枚举：0-隐藏，1-显示
     */
    private Integer status;

    /**
     * 菜单资源id
     */
    private Long menuResourceId;

    /**
     * 构造函数
     *
     * @param typeList
     * @param appId
     * @param name
     * @param status
     * @param menuResourceId
     */
    public BusinessResourceListQueryParamVO(List<String> typeList, Long appId, String name, Integer status, Long menuResourceId) {
        this.typeList = typeList;
        this.appId = appId;
        this.name = name;
        this.status = status;
        this.menuResourceId = menuResourceId;
    }

}
