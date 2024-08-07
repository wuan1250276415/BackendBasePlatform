package pro.wuan.privilege.vo;

import lombok.Data;

/**
 * 条件元素
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class ConditionElement {

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 条件符号。枚举：=-等于，!=-不等于，>-大于，>=大于等于，<-小于，<=-小于等于
     */
    private String conditionSymbol;

    /**
     * 字段值
     */
    private String columnValue;

}
