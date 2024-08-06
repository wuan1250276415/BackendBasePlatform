package pro.wuan.feignapi.userapi.vo;

import lombok.Data;

import java.util.List;

/**
 * 单位树形VO
 *
 * @author: liumin
 * @date: 2021/9/7 15:58
 */
@Data
public class UnitTreeVO {

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
     * 备注
     */
    private String remark;

    /**
     * 下级列表
     */
    private List<UnitTreeVO> children;

}
