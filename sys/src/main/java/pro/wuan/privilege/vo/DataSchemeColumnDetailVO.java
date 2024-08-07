package pro.wuan.privilege.vo;

import lombok.Data;

import java.util.List;

/**
 * 数据方案字段详情VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class DataSchemeColumnDetailVO {

    /**
     * 字段名称
     */
    private String columnName;

    /**
     * 字段注解
     */
    private String columnCode;

    /**
     * 字段类型。枚举：String，Integer，Boolean，Double
     */
    private String columnType;

    /**
     * 条件符号。枚举：=-等于，!=-不等于，>-大于，>=大于等于，<-小于，<=-小于等于
     */
    private List<String> conditionSymbolList;

    /**
     * 条件内容。枚举：anyText-任意文本，@currentUserId-当前用户，@currentUnitId-当前单位，@currentDeptId-当前部门
     */
    private String conditionContent;

}
