package pro.wuan.common.web.validator.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.List;

/**
 * 字段校验规则信息
 *
 */
@Data
@ToString
@Accessors(chain = true)
public class FieldValidatorDesc {
    /**
     * 字段名称
     */
    private String field;
    /**
     * 字段的类型
     */
    private String fieldType;
    /**
     * 约束集合
     */
    private List<ConstraintInfo> constraints;

    /**
     * 子表限制,只有不为空的时候才显示
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<FieldValidatorDesc> children;
}
