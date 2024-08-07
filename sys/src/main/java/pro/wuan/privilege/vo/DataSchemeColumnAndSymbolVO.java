package pro.wuan.privilege.vo;

import lombok.Data;

/**
 * 数据方案字段及条件符号关联表VO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class DataSchemeColumnAndSymbolVO {

    /**
     * 字段id
     */
    private String columnId;

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
    private String conditionSymbol;

    /**
     * 条件内容。枚举：anyText-任意文本，@currentUserId-当前用户，@currentUnitId-当前单位，@currentDeptId-当前部门
     */
    private String conditionContent;

}
