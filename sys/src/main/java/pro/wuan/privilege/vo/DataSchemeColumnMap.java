package pro.wuan.privilege.vo;

import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * 数据方案字段信息集合
 *
 * @author: liumin
 * @date: 2021/9/13 10:32
 */
@Data
public class DataSchemeColumnMap {

    /**
     * 字段名称列表
     */
    private List<String> columnNameList;

    /**
     * 字段名称、字段详情集合
     */
    private Map<String, DataSchemeColumnDetailVO> columnDetailMap;

}
