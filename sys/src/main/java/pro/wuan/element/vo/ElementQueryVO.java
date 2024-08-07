package pro.wuan.element.vo;

import lombok.Data;

/**
 * 元素查询VO
 *
 * @author: liumin
 * @date: 2021/9/7 15:58
 */
@Data
public class ElementQueryVO {

    /**
     * 元素名称
     */
    private String elementName;

    /**
     * 元素类型。包括：unit-单位，dept-部门，post-岗位，user-用户，role-角色
     */
    private String elementType;

}
