package pro.wuan.privilege.vo;

import lombok.Data;

/**
 * 数据方案字段及条件符号关联表idVO
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class DataSchemeColumnAndSymbolIdVO {

    /**
     * 数据方案字段id
     */
    private Long dataSchemeColumnId;

    /**
     * 数据方案字段、条件符号关联表id
     */
    private Long dataSchemeColumnSymbolId;

}
