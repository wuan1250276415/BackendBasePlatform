package pro.wuan.feignapi.dictapi.model;

import lombok.Data;

/** 数据字典查询参数
 * @author: oldone
 * @date: 2021/9/16 16:47
 */
@Data
public class DictParams {
    /**
     * 父节点id
     */
    private Long parentId;
    /**
     * 是否包含子节点
     */
    private Boolean hasChildren;
    /**
     * 是否显示
     */
    private String displayFlag;
    /**
     * 字典名称
     */
    private String name;
    /**
     * 字典值
     */
    private String value;
    /**
     * 排序字段
     */
    private String sortField;
    /**
     * 排序方式
     */
    private String sortOrder;
}
