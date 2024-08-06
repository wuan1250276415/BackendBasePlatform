package pro.wuan.common.web.validator.extract;


import pro.wuan.common.web.validator.model.FieldValidatorDesc;
import pro.wuan.common.web.validator.model.ValidConstraint;

import java.util.Collection;
import java.util.List;


/**
 * 提取指定表单验证规则
 *
 */
public interface IConstraintExtract {

    /**
     * 提取指定表单验证规则
     *
     * @param constraints 限制条件
     * @return 验证规则
     * @throws Exception 异常
     */
    Collection<FieldValidatorDesc> extract(List<ValidConstraint> constraints) throws Exception;
}
