package pro.wuan.feignapi.userapi.vo;

import lombok.Data;

import java.util.List;

/**
 * 组织机构树形VO
 *
 * @author: liumin
 * @date: 2021/9/7 15:58
 */
@Data
public class OrganTreeVO {

    /**
     * id
     */
    private Long id;

    /**
     * 编码
     */
    private String code;

    /**
     * 名称
     */
    private String name;

    /**
     * 父id
     */
    private Long parentId;

    /**
     * 是否单位
     */
    private boolean unit;

    /**
     * 下级列表
     */
    private List<OrganTreeVO> children;

}
