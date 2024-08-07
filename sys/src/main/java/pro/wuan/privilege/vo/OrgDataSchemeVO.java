package pro.wuan.privilege.vo;

import lombok.Data;
import pro.wuan.feignapi.userapi.entity.OrgDataSchemeModel;

import java.util.List;

/**
 * 数据方案VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class OrgDataSchemeVO extends OrgDataSchemeModel {

    /**
     * 条件分组列表
     */
    private List<ConditionGroup> conditionGroupList;

}
