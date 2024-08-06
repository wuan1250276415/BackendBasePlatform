package pro.wuan.common.core.model;

import lombok.Data;

@Data
public class BaseQueryValue {

    /**
     *查询规则 =  like  <=  >=
     */
    private String opt;


    /**
     *属性值
     */
    private Object[] values;
}
