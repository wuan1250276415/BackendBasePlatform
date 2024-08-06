package pro.wuan.common.web.validator.mateconstraint.impl;


import pro.wuan.common.web.validator.mateconstraint.IConstraintConverter;
import pro.wuan.common.web.validator.utils.ValidatorConstants;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;

/**
 * 其他 转换器
 *
 */
public class OtherConstraintConverter extends BaseConstraintConverter implements IConstraintConverter {

    @Override
    protected String getType(Class<? extends Annotation> type) {
        return type.getSimpleName();
    }

    @Override
    protected List<Class<? extends Annotation>> getSupport() {
        return new ArrayList<>();
    }


    @Override
    protected List<String> getMethods() {
        return List.of(ValidatorConstants.MESSAGE);
    }
}
