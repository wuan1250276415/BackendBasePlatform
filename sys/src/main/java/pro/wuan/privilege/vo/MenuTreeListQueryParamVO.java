package pro.wuan.privilege.vo;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * 菜单树形列表查询参数VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class MenuTreeListQueryParamVO {

    /**
     * 应用id
     */
    @NotNull(message = "应用id不能为空")
    private Long appId;

    /**
     * 名称
     */
    private String name;

    /**
     * 显示状态。枚举：0-隐藏，1-显示
     */
    private Integer status;

}
