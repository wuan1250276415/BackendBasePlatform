package pro.wuan.common.web.validator.mateconstraint.impl;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Pattern;
import org.hibernate.validator.constraints.URL;
import pro.wuan.common.core.annotation.constraints.annotation.NotEmptyPattern;
import pro.wuan.common.web.validator.mateconstraint.IConstraintConverter;
import pro.wuan.common.web.validator.utils.ValidatorConstants;

import java.lang.annotation.Annotation;
import java.util.Arrays;
import java.util.List;

/**
 * 正则校验规则
 *
 */
public class RegExConstraintConverter extends BaseConstraintConverter implements IConstraintConverter {
    @Override
    protected String getType(Class<? extends Annotation> type) {
        return "RegEx";
    }

    @Override
    protected List<Class<? extends Annotation>> getSupport() {
        return Arrays.asList(Pattern.class, Email.class, URL.class, NotEmptyPattern.class);
    }

    @Override
    protected List<String> getMethods() {
        return Arrays.asList("regexp", ValidatorConstants.MESSAGE);
    }
}
