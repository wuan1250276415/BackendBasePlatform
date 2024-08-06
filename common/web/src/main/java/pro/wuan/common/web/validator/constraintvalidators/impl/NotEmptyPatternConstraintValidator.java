package pro.wuan.common.web.validator.constraintvalidators.impl;

import cn.hutool.core.util.StrUtil;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.hibernate.validator.constraintvalidation.HibernateConstraintValidatorContext;
import org.hibernate.validator.internal.engine.messageinterpolation.util.InterpolationHelper;
import org.hibernate.validator.internal.util.logging.Log;
import org.hibernate.validator.internal.util.logging.LoggerFactory;
import pro.wuan.common.core.annotation.constraints.annotation.NotEmptyPattern;

import java.lang.invoke.MethodHandles;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * NotEmptyPattern注解的 约束验证器
 *
 */
public class NotEmptyPatternConstraintValidator implements ConstraintValidator<NotEmptyPattern, CharSequence> {
    private static final Log LOG = LoggerFactory.make(MethodHandles.lookup());
    private Pattern pattern;
    private String escapedRegexp;

    @Override
    public void initialize(NotEmptyPattern parameters) {
        NotEmptyPattern.Flag[] flags = parameters.flags();
        int intFlag = 0;
        for (NotEmptyPattern.Flag flag : flags) {
            intFlag = intFlag | flag.getValue();
        }

        try {
            pattern = Pattern.compile(parameters.regexp(), intFlag);
        } catch (PatternSyntaxException e) {
            throw LOG.getInvalidRegularExpressionException(e);
        }

        escapedRegexp = InterpolationHelper.escapeMessageParameter(parameters.regexp());
    }

    @Override
    public boolean isValid(CharSequence value, ConstraintValidatorContext constraintValidatorContext) {
        if (StrUtil.isEmpty(value)) {
            return true;
        }

        if (constraintValidatorContext instanceof HibernateConstraintValidatorContext) {
            constraintValidatorContext.unwrap(HibernateConstraintValidatorContext.class).addMessageParameter("regexp", escapedRegexp);
        }

        Matcher m = pattern.matcher(value);
        return m.matches();
    }
}
