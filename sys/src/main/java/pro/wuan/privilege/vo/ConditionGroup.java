package pro.wuan.privilege.vo;

import lombok.Data;

import java.util.List;

/**
 * 条件分组
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class ConditionGroup {

    /**
     * 操作。and/or
     */
    private String operation;

    /**
     * 条件元素列表
     */
    private List<ConditionElement> elementList;

}
