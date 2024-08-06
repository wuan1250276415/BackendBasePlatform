package pro.wuan.common.web.validator.mateconstraint.impl;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import pro.wuan.common.web.validator.annotation.WanYuanToYuanMax;
import pro.wuan.common.web.validator.annotation.WanYuanToYuanMin;
import pro.wuan.common.web.validator.annotation.YuanMax;
import pro.wuan.common.web.validator.annotation.YuanMin;
import pro.wuan.common.web.validator.mateconstraint.IConstraintConverter;
import pro.wuan.common.web.validator.utils.ValidatorConstants;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * 长度 转换器
 */
public class MaxMinConstraintConverter extends BaseConstraintConverter implements IConstraintConverter {

    @Override
    protected List<String> getMethods() {
        return Arrays.asList("value", ValidatorConstants.MESSAGE);
    }

    @Override
    protected String getType(Class<? extends Annotation> type) {
        return type.getSimpleName();
    }

    @Override
    protected List<Class<? extends Annotation>> getSupport() {
        return Arrays.asList(Max.class, Min.class, DecimalMax.class, DecimalMin.class, WanYuanToYuanMax.class, WanYuanToYuanMin.class, YuanMax.class, YuanMin.class);
    }

}
