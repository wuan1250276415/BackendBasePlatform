package pro.wuan.common.web.validator.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import jakarta.validation.constraints.DecimalMax;
import pro.wuan.common.web.validator.constraintvalidators.impl.YuanMaxConstraintValidator;

import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.math.RoundingMode;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 元转元最大值后台校验, 转成元后保留2位精度，四舍五入
 *
 * @program: tellhowcloud
 * @author: HawkWang
 * @create: 2021-12-26 07:20
 **/
@Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
@Retention(RUNTIME)
@Repeatable(YuanMax.List.class)
@Documented
@Constraint(validatedBy = YuanMaxConstraintValidator.class)
public @interface YuanMax {

    String message() default "{javax.validation.constraints.DecimalMax.message}";

    Class<?>[] groups() default { };

    Class<? extends Payload>[] payload() default { };

    /**
     * The {@code String} representation of the max value according to the
     * {@code BigDecimal} string representation.
     *
     * @return value the element must be lower or equal to
     */
    String value() default "99999999999.99";


    /**
     * Specifies whether the specified maximum is inclusive or exclusive.
     * By default, it is inclusive.
     *
     * @return {@code true} if the value must be lower or equal to the specified maximum,
     *         {@code false} if the value must be lower
     *
     * @since 1.1
     */
    boolean inclusive() default true;

    /**
     * 小数位精度，默认到分,与数据库字段类型同步，避免用户输入与数据库的存储因四舍五入造成不一致
     * @return
     */
    int scale() default 2;

    /**
     * 转换成元之后的小数后的取值， 默认四舍五入
     * @return
     */
    RoundingMode roundingMode() default RoundingMode.HALF_UP;

    /**
     * Defines several {@link DecimalMax} annotations on the same element.
     *
     * @see DecimalMax
     */
    @Target({ METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE })
    @Retention(RUNTIME)
    @Documented
    @interface List {

        YuanMax[] value();
    }

}
