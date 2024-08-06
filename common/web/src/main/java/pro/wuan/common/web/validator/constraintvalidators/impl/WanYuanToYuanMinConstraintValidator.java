package pro.wuan.common.web.validator.constraintvalidators.impl;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import pro.wuan.common.web.validator.annotation.WanYuanToYuanMin;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * 万元转元最小值，转成元后，2位精度，四舍五入，配合统一后台校验
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-12-26 07:16
 **/
public class WanYuanToYuanMinConstraintValidator implements ConstraintValidator<WanYuanToYuanMin, BigDecimal> {

    protected BigDecimal minValue;
    private boolean inclusive;
    private int scale;
    private RoundingMode roundingMode;

    @Override
    public void initialize(WanYuanToYuanMin constraintAnnotation) {
        try {
            BigDecimal bigDecimal = new BigDecimal(constraintAnnotation.value());
            this.roundingMode = constraintAnnotation.roundingMode();
            this.scale = constraintAnnotation.scale();
            if (getNumberOfDecimalPlace(bigDecimal) > this.scale) {
                throw new IllegalArgumentException(String.format("%s的小数精度必须小于等于%s", constraintAnnotation.value(), this.scale));
            }
            this.minValue = bigDecimal.movePointRight(4).setScale(this.scale > 4 ? this.scale -4 : 2, roundingMode);
        } catch (NumberFormatException nfe) {
            throw new IllegalArgumentException(String.format("%s不是有效的万元格式的数据。", constraintAnnotation.value()));
        }
        this.inclusive = constraintAnnotation.inclusive();
    }

    private int getNumberOfDecimalPlace(BigDecimal value) {
        final String s = value.toPlainString();
        final int index = s.indexOf('.');
        if (index < 0) {
            return 0;
        }
        return s.length() - 1 - index;
    }

    /**
     * Implements the validation logic.
     * The state of {@code value} must not be altered.
     * <p>
     * This method can be accessed concurrently, thread-safety must be ensured
     * by the implementation.
     *
     * @param value   object to validate
     * @param context context in which the constraint is evaluated
     * @return {@code false} if {@code value} does not pass the constraint
     */
    @Override
    public boolean isValid(BigDecimal value, ConstraintValidatorContext context) {
        // null values are valid
        if (value == null) {
            return true;
        }

        int comparisonResult = value.compareTo(minValue);
        return inclusive ? comparisonResult >= 0 : comparisonResult > 0;
    }
}
